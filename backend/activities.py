
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session, selectinload
from datetime import datetime, timezone
from database import get_db
from dependencies import require_role
from sqlalchemy import func, or_, select
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
    ActivityCreate,
    ActivityResponse,
    ActivityUpdate,
    ManagedActivityRegistrationResponse,
)

router = APIRouter(prefix="/activities", tags=["Actividades"])


def get_campaign_or_404(db: Session, campaign_id: int) -> Campaign:
    campaign = db.get(Campaign, campaign_id)
    if campaign is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Campaña no encontrada",
        )
    return campaign


def ensure_campaign_access(campaign: Campaign, current_user: User) -> None:
    is_admin = current_user.role == "admin"
    is_owner = campaign.created_by_user_id == current_user.id

    if not is_admin and not is_owner:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para gestionar esta campaña",
        )


def get_managed_activity_or_404(
    db: Session,
    activity_id: int,
    current_user: User,
) -> Activity:
    activity = db.get(Activity, activity_id)
    if activity is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    if current_user.role == "admin":
        return activity

    campaign = (
        db.get(Campaign, activity.campaign_id)
        if activity.campaign_id is not None
        else None
    )
    owns_activity = activity.created_by_user_id == current_user.id
    owns_campaign = (
        campaign is not None
        and campaign.created_by_user_id == current_user.id
    )
    if not (owns_activity and (campaign is None or owns_campaign)):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para gestionar esta actividad",
        )
    return activity


def ensure_compatible_datetimes(*values: datetime | None) -> None:
    """Rechaza mezclas de fechas con y sin zona horaria."""
    awareness = {
        value.tzinfo is not None and value.utcoffset() is not None
        for value in values
        if value is not None
    }

    if len(awareness) > 1:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_CONTENT,
            detail=(
                "Las fechas deben usar un formato consistente "
                "con o sin zona horaria"
            ),
        )


def ensure_utc(value: datetime) -> datetime:
    if value.tzinfo is None:
        return value.replace(tzinfo=timezone.utc)
    return value.astimezone(timezone.utc)


def build_activity_response(
    db: Session,
    activity: Activity,
) -> ActivityResponse:
    """Construye la respuesta de una actividad con su ocupación real."""

    # Cada inscripción confirmada cuenta un titular.
    titulares = db.scalar(
        select(func.count(Registration.id)).where(
            Registration.activity_id == activity.id,
            Registration.status == "confirmada",
        )
    ) or 0

    # Los participantes asociados son acompañantes del titular.
    acompanantes = db.scalar(
        select(func.count(FamilyMember.id))
        .select_from(Registration)
        .join(Registration.participants)
        .where(
            Registration.activity_id == activity.id,
            Registration.status == "confirmada",
        )
    ) or 0

    response = ActivityResponse.model_validate(activity)

    return response.model_copy(
        update={"confirmed_people": titulares + acompanantes}
    )



@router.get("", response_model=list[ActivityResponse])
def list_activities(db: Session = Depends(get_db)):
    """Lista actividades públicas independientes o de campañas publicadas."""
    statement = (
        select(Activity)
        .outerjoin(Campaign, Activity.campaign_id == Campaign.id)
        .where(
            Activity.status == "publicada",
            or_(
                Activity.campaign_id.is_(None),
                Campaign.status == "publicada",
            ),
        )
        .order_by(Activity.starts_at)
    )

    activities = db.scalars(statement).all()

    return [
        build_activity_response(db, activity)
        for activity in activities
    ]


@router.get("/admin", response_model=list[ActivityResponse])
def list_managed_activities(
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    """Lista todas las actividades que la organización puede gestionar."""
    statement = (
        select(Activity)
        .outerjoin(Campaign, Activity.campaign_id == Campaign.id)
        .order_by(Activity.starts_at)
    )
    if current_user.role != "admin":
        statement = statement.where(
            or_(
                Activity.created_by_user_id == current_user.id,
                Campaign.created_by_user_id == current_user.id,
            )
        )

    activities = db.scalars(statement).all()
    return [
        build_activity_response(db, activity)
        for activity in activities
    ]



@router.get("/{activity_id}", response_model=ActivityResponse)
def get_activity(activity_id: int, db: Session = Depends(get_db)):
    """Consulta una actividad disponible para el público."""
    activity = db.get(Activity, activity_id)

    if activity is None or activity.status != "publicada":
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    campaign = (
        db.get(Campaign, activity.campaign_id)
        if activity.campaign_id is not None
        else None
    )

    if activity.campaign_id is not None and (
        campaign is None or campaign.status != "publicada"
    ):
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    return build_activity_response(db, activity)


@router.get(
    "/admin/{activity_id}/registrations",
    response_model=list[ManagedActivityRegistrationResponse],
)
def list_activity_registrations(
    activity_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    activity = get_managed_activity_or_404(db, activity_id, current_user)
    rows = db.execute(
        select(Registration, Family.name, User.full_name, User.email, User.phone)
        .join(Family, Registration.family_id == Family.id)
        .join(User, Family.owner_user_id == User.id)
        .options(selectinload(Registration.participants))
        .where(Registration.activity_id == activity.id)
        .order_by(Registration.created_at.desc())
    ).all()

    return [
        ManagedActivityRegistrationResponse(
            id=registration.id,
            activity_id=registration.activity_id,
            family_id=registration.family_id,
            family_name=family_name,
            contact_name=contact_name,
            email=email,
            phone=phone,
            status=registration.status,
            participant_names=[
                participant.full_name
                for participant in registration.participants
            ],
            people_count=1 + len(registration.participants),
            created_at=registration.created_at,
        )
        for registration, family_name, contact_name, email, phone in rows
    ]


@router.patch(
    "/admin/{activity_id}/registrations/{registration_id}/cancel",
    response_model=list[ManagedActivityRegistrationResponse],
)
def cancel_activity_registration(
    activity_id: int,
    registration_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    activity = get_managed_activity_or_404(db, activity_id, current_user)
    registration = db.scalar(
        select(Registration)
        .where(
            Registration.id == registration_id,
            Registration.activity_id == activity.id,
        )
        .with_for_update()
    )
    if registration is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Inscripción no encontrada para esta actividad",
        )
    if registration.status == "cancelada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La inscripción ya está cancelada",
        )

    previous_status = registration.status
    registration.status = "cancelada"
    registration.canceled_at = datetime.now(timezone.utc)
    registration.updated_at = registration.canceled_at
    db.add(
        RegistrationEvent(
            registration_id=registration.id,
            event_type="inscripcion_cancelada",
            from_status=previous_status,
            to_status="cancelada",
            actor_user_id=current_user.id,
            details="Cancelada por la organización",
        )
    )

    db.commit()
    return list_activity_registrations(activity_id, db, current_user)


@router.patch(
    "/admin/{activity_id}/registrations/{registration_id}/accept",
    response_model=list[ManagedActivityRegistrationResponse],
)
def accept_waitlisted_registration(
    activity_id: int,
    registration_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    get_managed_activity_or_404(db, activity_id, current_user)
    activity = db.scalar(
        select(Activity)
        .where(Activity.id == activity_id)
        .with_for_update()
    )
    if activity is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )
    if ensure_utc(activity.starts_at) <= datetime.now(timezone.utc):
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se puede confirmar una inscripción después del inicio de la actividad",
        )

    registration = db.scalar(
        select(Registration)
        .where(
            Registration.id == registration_id,
            Registration.activity_id == activity.id,
        )
        .with_for_update()
        .options(selectinload(Registration.participants))
    )
    if registration is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Inscripción no encontrada para esta actividad",
        )
    if registration.status != "lista_espera":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La inscripción ya no está en lista de espera",
        )

    confirmed = db.scalars(
        select(Registration)
        .where(
            Registration.activity_id == activity.id,
            Registration.status == "confirmada",
        )
        .options(selectinload(Registration.participants))
    ).all()
    occupied_people = sum(
        1 + len(item.participants)
        for item in confirmed
    )
    required_people = 1 + len(registration.participants)
    if occupied_people + required_people > activity.capacity_people:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No hay suficientes lugares disponibles para esta familia",
        )

    registration.status = "confirmada"
    registration.updated_at = datetime.now(timezone.utc)
    db.add(
        RegistrationEvent(
            registration_id=registration.id,
            event_type="lista_espera_aceptada",
            from_status="lista_espera",
            to_status="confirmada",
            actor_user_id=current_user.id,
            details="Confirmada manualmente por la organización",
        )
    )
    db.commit()
    return list_activity_registrations(activity_id, db, current_user)


@router.delete(
    "/{activity_id}",
    status_code=status.HTTP_204_NO_CONTENT,
)
def cancel_activity(
    activity_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    activity = get_managed_activity_or_404(db, activity_id, current_user)
    if activity.status == "cancelada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La actividad ya está cancelada",
        )
    if activity.status == "finalizada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se puede cancelar una actividad finalizada",
        )
    activity.status = "cancelada"
    db.commit()


@router.post(
    "",
    response_model=ActivityResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_activity(
    data: ActivityCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    """Crea una actividad independiente o vinculada a una campaña propia."""
    campaign = None
    if data.campaign_id is not None:
        campaign = get_campaign_or_404(db, data.campaign_id)
        ensure_campaign_access(campaign, current_user)

        if campaign.status != "publicada":
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail="Solo se pueden agregar actividades a campañas publicadas",
            )

    activity = Activity(
        campaign_id=campaign.id if campaign is not None else None,
        title=data.title,
        description=data.description,
        photo_url=data.photo_url,
        starts_at=data.starts_at,
        ends_at=data.ends_at,
        location=data.location,
        capacity_people=data.capacity_people,
        registration_deadline=data.registration_deadline,
        status="borrador",
        created_by_user_id=current_user.id,
    )

    db.add(activity)
    db.commit()
    db.refresh(activity)

    return build_activity_response(db, activity)



@router.patch("/{activity_id}", response_model=ActivityResponse)
def update_activity(
    activity_id: int,
    data: ActivityUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    """Actualiza una actividad propia o cualquier actividad como admin."""
    activity = db.get(Activity, activity_id)

    if activity is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    campaign = (
        get_campaign_or_404(db, activity.campaign_id)
        if activity.campaign_id is not None
        else None
    )

    is_admin = current_user.role == "admin"
    owns_activity = activity.created_by_user_id == current_user.id
    owns_campaign = (
        campaign is not None
        and campaign.created_by_user_id == current_user.id
    )

    if not is_admin and not (
        owns_activity and (campaign is None or owns_campaign)
    ):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para modificar esta actividad",
        )

    changes = data.model_dump(exclude_unset=True, exclude_none=True)

    # Validar las fechas resultantes, incluso las que no se modifican.
    final_starts_at = changes.get("starts_at", activity.starts_at)
    final_ends_at = changes.get("ends_at", activity.ends_at)
    final_deadline = changes.get(
        "registration_deadline",
        activity.registration_deadline,
    )

    ensure_compatible_datetimes(
        final_starts_at,
        final_ends_at,
        final_deadline,
    )

    if final_ends_at is not None and final_ends_at <= final_starts_at:
        raise HTTPException(
            status_code=422,
            detail="La fecha de finalización debe ser posterior al inicio",
        )

    if final_deadline is not None and final_deadline > final_starts_at:
        raise HTTPException(
            status_code=422,
            detail="La fecha límite de inscripción no puede ser posterior al inicio",
        )

    if "status" in changes:
        if not is_admin:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Solo un administrador puede cambiar el estado de una actividad",
            )

        allowed_transitions = {
            "borrador": {"publicada", "cancelada"},
            "publicada": {"cancelada", "finalizada"},
            "cancelada": set(),
            "finalizada": set(),
        }

        new_status = changes["status"]

        if new_status == activity.status:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail=f"La actividad ya está en estado '{activity.status}'",
            )

        if new_status not in allowed_transitions.get(activity.status, set()):
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail=(
                    f"No se puede cambiar una actividad de "
                    f"'{activity.status}' a '{new_status}'"
                ),
            )

        if new_status == "publicada":
            if campaign is not None and campaign.status != "publicada":
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="La campaña debe estar publicada para publicar la actividad",
                )

    for field, value in changes.items():
        setattr(activity, field, value)

    db.commit()
    db.refresh(activity)

    return build_activity_response(db, activity)
