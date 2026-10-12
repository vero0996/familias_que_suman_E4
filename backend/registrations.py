
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


def count_confirmed(db: Session, activity_id: int) -> int:
    return db.scalar(
        select(func.count(Registration.id)).where(
            Registration.activity_id == activity_id,
            Registration.status == "confirmada",
        )
    ) or 0


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
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
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

    confirmed = count_confirmed(db, activity.id)
    new_status = (
        "confirmada"
        if confirmed < activity.capacity_families
        else "lista_espera"
    )

    try:
        if existing is None:
            registration = Registration(
                activity_id=activity.id,
                family_id=family.id,
                status=new_status,
                notes=data.notes,
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

    # Lock the registration and activity to coordinate cancellations
    # with registrations competing for the same capacity.
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

    if registration.family_id != family.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para cancelar esta inscripción",
        )

    if registration.status == "cancelada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La inscripción ya está cancelada",
        )

    activity_id = registration.activity_id
    old_status = registration.status

    # Lock the activity row before changing seat availability.
    activity = db.scalar(
        select(Activity)
        .where(Activity.id == activity_id)
        .with_for_update()
    )

    registration.status = "cancelada"
    registration.canceled_at = utc_now()
    registration.updated_at = utc_now()

    reason = data.reason if data else None
    add_event(
        db,
        registration,
        "inscripcion_cancelada",
        current_user.id,
        from_status=old_status,
        details=reason,
    )

    # A confirmed cancellation frees one seat. Promote the oldest
    # waiting family, provided the activity has not started.
    if (
        old_status == "confirmada"
        and activity is not None
        and ensure_utc(activity.starts_at) > utc_now()
    ):
        waiting = db.scalar(
            select(Registration)
            .where(
                Registration.activity_id == activity_id,
                Registration.status == "lista_espera",
            )
            .order_by(Registration.created_at, Registration.id)
            .limit(1)
            .with_for_update()
        )

        if waiting is not None:
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

    db.commit()
    db.refresh(registration)
    return registration
