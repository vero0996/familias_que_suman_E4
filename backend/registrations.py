from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import func, select
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import (
    Activity,
    Campaign,
    Family,
    FamilyMember,
    Notification,
    Registration,
    RegistrationEvent,
    User,
)
from schemas import (
    RegistrationCancelRequest,
    RegistrationCreate,
    RegistrationResponse,
)


router = APIRouter(prefix="/registrations", tags=["Inscripciones"])


def get_family_or_409(db: Session, user_id: int) -> Family:
    family = db.scalar(
        select(Family).where(Family.owner_user_id == user_id)
    )

    if family is None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Tu cuenta no tiene una familia configurada",
        )

    return family


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


def ensure_utc(value: datetime) -> datetime:
    if value.tzinfo is None:
        return value.replace(tzinfo=timezone.utc)

    return value.astimezone(timezone.utc)


def get_registration_or_404(
    db: Session,
    registration_id: int,
) -> Registration:
    registration = db.get(Registration, registration_id)

    if registration is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Inscripción no encontrada",
        )

    return registration


def add_event(
    db: Session,
    registration: Registration,
    event_type: str,
    actor_user_id: int,
    from_status: str | None = None,
    details: str | None = None,
) -> None:
    db.add(
        RegistrationEvent(
            registration_id=registration.id,
            event_type=event_type,
            from_status=from_status,
            to_status=registration.status,
            actor_user_id=actor_user_id,
            details=details,
        )
    )


def add_notification(
    db: Session,
    user_id: int,
    title: str,
    message: str,
    notification_type: str,
) -> None:
    db.add(
        Notification(
            user_id=user_id,
            title=title,
            message=message,
            notification_type=notification_type,
        )
    )


def get_available_activity(
    db: Session,
    activity_id: int,
    lock: bool = False,
) -> Activity:
    statement = select(Activity).where(Activity.id == activity_id)

    if lock:
        statement = statement.with_for_update()

    activity = db.scalar(statement)

    if activity is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    campaign = (
        db.get(Campaign, activity.campaign_id)
        if activity.campaign_id is not None
        else None
    )

    if (
        activity.status != "publicada"
        or campaign is None
        or campaign.status != "publicada"
    ):
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La actividad no está disponible para inscripciones",
        )

    now = utc_now()

    if ensure_utc(activity.starts_at) <= now:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La actividad ya comenzó o ya pasó",
        )

    if ensure_utc(activity.registration_deadline) <= now:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="El periodo de inscripción terminó",
        )

    return activity


def count_confirmed_people(db: Session, activity_id: int) -> int:
    """Cuenta titulares y acompañantes de las inscripciones confirmadas."""

    # Cada inscripción confirmada incluye un titular.
    titulares = db.scalar(
        select(func.count(Registration.id)).where(
            Registration.activity_id == activity_id,
            Registration.status == "confirmada",
        )
    ) or 0

    # Cada integrante asociado a la inscripción es un acompañante.
    acompanantes = db.scalar(
        select(func.count(FamilyMember.id))
        .select_from(Registration)
        .join(Registration.participants)
        .where(
            Registration.activity_id == activity_id,
            Registration.status == "confirmada",
        )
    ) or 0

    return titulares + acompanantes


@router.post(
    "",
    response_model=RegistrationResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_registration(
    data: RegistrationCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    # Lock the activity row so simultaneous registrations cannot
    # exceed capacity by counting the same available seat.
    activity = get_available_activity(db, data.activity_id, lock=True)
    family = get_family_or_409(db, current_user.id)

    participants = db.scalars(
        select(FamilyMember).where(
            FamilyMember.id.in_(data.participant_ids),
            FamilyMember.family_id == family.id,
            FamilyMember.is_active.is_(True),
        )
    ).all()

    if len(participants) != len(data.participant_ids):
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_CONTENT,
            detail="Todos los integrantes deben pertenecer a tu familia y estar activos",
        )

    existing = db.scalar(
        select(Registration).where(
            Registration.activity_id == activity.id,
            Registration.family_id == family.id,
        )
    )

    if existing is not None and existing.status != "cancelada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Tu familia ya tiene una inscripción activa para esta actividad",
        )

    confirmed_people = count_confirmed_people(db, activity.id)

    # El titular de la familia también ocupa un lugar.
    # participant_ids contiene únicamente a los acompañantes seleccionados.
    requested_people = 1 + len(participants)

    new_status = (
        "confirmada"
        if confirmed_people + requested_people <= activity.capacity_people
        else "lista_espera"
    )
    waitlisted_at = utc_now() if new_status == "lista_espera" else None

    try:
        if existing is None:
            registration = Registration(
                activity_id=activity.id,
                family_id=family.id,
                status=new_status,
                notes=data.notes,
                waitlisted_at=waitlisted_at,
            )

            db.add(registration)
            db.flush()

            registration.participants = list(participants)

            add_event(
                db,
                registration,
                "inscripcion_creada",
                current_user.id,
                from_status=None,
                details=f"Estado inicial: {new_status}",
            )

        else:
            old_status = existing.status
            existing.status = new_status
            existing.waitlisted_at = waitlisted_at
            existing.notes = data.notes
            existing.canceled_at = None
            existing.participants = list(participants)
            existing.updated_at = utc_now()
            registration = existing

            add_event(
                db,
                registration,
                "inscripcion_reactivada",
                current_user.id,
                from_status=old_status,
                details=f"Nuevo estado: {new_status}",
            )

        if new_status == "confirmada":
            add_notification(
                db,
                current_user.id,
                "Confirmación de inscripción",
                f"Tu inscripción a {activity.title} fue confirmada.",
                "registration_confirmed",
            )

        db.commit()
        db.refresh(registration)

        return registration

    except IntegrityError:
        db.rollback()

        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo completar la inscripción. Intenta de nuevo.",
        )


@router.get("/me", response_model=list[RegistrationResponse])
def list_my_registrations(
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_family_or_409(db, current_user.id)

    return db.scalars(
        select(Registration)
        .where(Registration.family_id == family.id)
        .order_by(Registration.created_at.desc())
    ).all()


@router.get(
    "/{registration_id}",
    response_model=RegistrationResponse,
)
def get_my_registration(
    registration_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_family_or_409(db, current_user.id)
    registration = get_registration_or_404(db, registration_id)

    if registration.family_id != family.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para consultar esta inscripción",
        )

    return registration


@router.patch(
    "/{registration_id}/cancel",
    response_model=RegistrationResponse,
)
def cancel_registration(
    registration_id: int,
    data: RegistrationCancelRequest | None = None,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_family_or_409(db, current_user.id)

    # Read the registration without locking it yet, only to discover
    # which activity must be locked first.
    registration_ref = db.scalar(
        select(Registration).where(
            Registration.id == registration_id
        )
    )

    if registration_ref is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Inscripción no encontrada",
        )

    activity_id = registration_ref.activity_id

    # Use the same lock order as registration creation:
    # activity first, registration second.
    activity = db.scalar(
        select(Activity)
        .where(Activity.id == activity_id)
        .with_for_update()
    )

    registration = db.scalar(
        select(Registration)
        .where(Registration.id == registration_id)
        .with_for_update()
    )

    if registration is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Inscripción no encontrada",
        )

    # Recheck ownership and status after acquiring the locks.
    if registration.family_id != family.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para cancelar esta inscripción",
        )

    if registration.activity_id != activity_id:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La inscripción cambió; vuelve a intentarlo",
        )

    if registration.status == "cancelada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La inscripción ya está cancelada",
        )

    old_status = registration.status
    now = utc_now()

    registration.status = "cancelada"
    registration.canceled_at = now
    registration.updated_at = now

    reason = data.reason if data else None

    add_event(
        db,
        registration,
        "inscripcion_cancelada",
        current_user.id,
        from_status=old_status,
        details=reason,
    )

    add_notification(
        db,
        current_user.id,
        "Cancelación",
        f"Tu inscripción a {activity.title} fue cancelada.",
        "registration_cancelled",
    )

    # Promote the oldest waiting family if a confirmed seat is freed
    # and the activity has not started.
    if (
        old_status == "confirmada"
        and activity is not None
        and ensure_utc(activity.starts_at) > utc_now()
    ):
        # Enviar la cancelación a la base antes de calcular el cupo.
        db.flush()

        confirmed_people = count_confirmed_people(db, activity_id)

        available_people = max(
            0,
            activity.capacity_people - confirmed_people,
        )

        waiting_registrations = db.scalars(
            select(Registration)
            .where(
                Registration.activity_id == activity_id,
                Registration.status == "lista_espera",
            )
            .order_by(
                func.coalesce(
                    Registration.waitlisted_at,
                    Registration.created_at,
                ),
                Registration.id,
            )
            .with_for_update()
        ).all()

        for waiting in waiting_registrations:
            # Cada familia incluye al titular y a sus acompañantes.
            people_count = 1 + len(waiting.participants)

            if people_count > available_people:
                continue

            previous_status = waiting.status
            waiting.status = "confirmada"
            waiting.updated_at = utc_now()

            add_event(
                db,
                waiting,
                "lista_espera_promovida",
                current_user.id,
                from_status=previous_status,
                details="Cupo liberado por una cancelación",
            )

            waiting_family = db.get(Family, waiting.family_id)

            if waiting_family is not None:
                add_notification(
                    db,
                    waiting_family.owner_user_id,
                    "Confirmación de inscripción",
                    f"Tu inscripción a {activity.title} fue confirmada.",
                    "registration_confirmed",
                )

            available_people -= people_count

    db.commit()
    db.refresh(registration)

    return registration