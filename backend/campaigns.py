
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from database import get_db
from dependencies import get_current_user, require_role
from models import Campaign, User
from schemas import CampaignCreate, CampaignResponse, CampaignUpdate

router = APIRouter(prefix="/campaigns", tags=["Campañas"])


@router.get("", response_model=list[CampaignResponse])
def list_campaigns(db: Session = Depends(get_db)):
    """Lista las campañas publicadas."""
    statement = (
        select(Campaign)
        .where(Campaign.status == "publicada")
        .order_by(Campaign.created_at.desc())
    )
    return db.scalars(statement).all()


@router.get("/{campaign_id}", response_model=CampaignResponse)
def get_campaign(campaign_id: int, db: Session = Depends(get_db)):
    """Consulta una campaña publicada por su ID."""
    campaign = db.get(Campaign, campaign_id)

    if campaign is None or campaign.status != "publicada":
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Campaña no encontrada",
        )

    return campaign


@router.post(
    "",
    response_model=CampaignResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_campaign(
    data: CampaignCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    """Crea una campaña como borrador."""
    campaign = Campaign(
        title=data.title,
        description=data.description,
        organization_name=data.organization_name,
        status="borrador",
        created_by_user_id=current_user.id,
    )

    db.add(campaign)
    db.commit()
    db.refresh(campaign)

    return campaign



@router.patch("/{campaign_id}", response_model=CampaignResponse)
def update_campaign(
    campaign_id: int,
    data: CampaignUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    """Actualiza una campaña propia o cualquier campaña como admin."""
    campaign = db.get(Campaign, campaign_id)

    if campaign is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Campaña no encontrada",
        )

    is_admin = current_user.role == "admin"
    is_owner = campaign.created_by_user_id == current_user.id

    if not is_admin and not is_owner:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para modificar esta campaña",
        )

    changes = data.model_dump(exclude_unset=True, exclude_none=True)

    # Solo el administrador puede cambiar el estado.
    if "status" in changes:
        if not is_admin:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Solo un administrador puede cambiar el estado de la campaña",
            )

        allowed_transitions = {
            "borrador": {"publicada", "cancelada"},
            "publicada": {"cancelada", "finalizada"},
            "cancelada": set(),
            "finalizada": set(),
        }

        new_status = changes["status"]

        
        if new_status == campaign.status:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail=f"La campaña ya está en estado '{campaign.status}'",
            )

        allowed = allowed_transitions.get(campaign.status, set())

        if new_status not in allowed:
            raise HTTPException(
                status_code=status.HTTP_409_CONFLICT,
                detail=(
                    f"No se puede cambiar una campaña de "
                    f"'{campaign.status}' a '{new_status}'"
                ),
            )


    for field, value in changes.items():
        setattr(campaign, field, value)

    db.commit()
    db.refresh(campaign)

    return campaign

