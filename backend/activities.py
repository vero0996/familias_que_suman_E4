
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session
from datetime import datetime
from database import get_db
from dependencies import require_role
from models import Activity, Campaign, User
from schemas import ActivityCreate, ActivityResponse, ActivityUpdate

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


@router.get("", response_model=list[ActivityResponse])
def list_activities(db: Session = Depends(get_db)):
    """Lista actividades publicadas de campañas publicadas."""
    statement = (
        select(Activity)
        .join(Campaign, Activity.campaign_id == Campaign.id)
        .where(
            Activity.status == "publicada",
            Campaign.status == "publicada",
        )
        .order_by(Activity.starts_at)
    )
    return db.scalars(statement).all()


@router.get("/{activity_id}", response_model=ActivityResponse)
def get_activity(activity_id: int, db: Session = Depends(get_db)):
    """Consulta una actividad disponible para el público."""
    activity = db.get(Activity, activity_id)

    if activity is None or activity.status != "publicada":
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    campaign = db.get(Campaign, activity.campaign_id) if activity.campaign_id else None

    if campaign is None or campaign.status != "publicada":
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    return activity


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
    """Crea una actividad en una campaña que el usuario puede gestionar."""
    campaign = get_campaign_or_404(db, data.campaign_id)
    ensure_campaign_access(campaign, current_user)

    if campaign.status != "publicada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Solo se pueden agregar actividades a campañas publicadas",
        )

    activity = Activity(
        campaign_id=campaign.id,
        title=data.title,
        description=data.description,
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

    return activity



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

    if not is_admin and not (owns_activity and owns_campaign):
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
            if campaign is None or campaign.status != "publicada":
                raise HTTPException(
                    status_code=status.HTTP_409_CONFLICT,
                    detail="La campaña debe estar publicada para publicar la actividad",
                )

    for field, value in changes.items():
        setattr(activity, field, value)

    db.commit()
    db.refresh(activity)

    return activity
