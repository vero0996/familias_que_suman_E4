
from datetime import datetime, timezone

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import Activity, Family, Registration, Survey, User
from schemas import SurveyCreate, SurveyResponse


router = APIRouter(prefix="/surveys", tags=["Encuestas"])


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


def ensure_utc(value: datetime) -> datetime:
    if value.tzinfo is None:
        return value.replace(tzinfo=timezone.utc)
    return value.astimezone(timezone.utc)


def get_my_family(db: Session, user_id: int) -> Family:
    family = db.scalar(
        select(Family).where(Family.owner_user_id == user_id)
    )
    if family is None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Tu cuenta no tiene una familia configurada",
        )
    return family


@router.post(
    "",
    response_model=SurveyResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_survey(
    data: SurveyCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_my_family(db, current_user.id)

    registration = db.get(Registration, data.registration_id)
    if registration is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Inscripción no encontrada",
        )

    if registration.family_id != family.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No puedes evaluar una actividad de otra familia",
        )

    if registration.status != "confirmada":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Solo puedes evaluar una inscripción confirmada",
        )

    activity = db.get(Activity, registration.activity_id)
    if activity is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    if (
        activity.ends_at is None
        or ensure_utc(activity.ends_at) > utc_now()
    ):
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Podrás evaluar la actividad cuando haya terminado",
        )

    existing = db.scalar(
        select(Survey).where(
            Survey.registration_id == registration.id
        )
    )
    if existing is not None:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="Esta inscripción ya tiene una evaluación",
        )

    survey = Survey(
        registration_id=registration.id,
        campaign_rating=data.campaign_rating,
        organization_rating=data.organization_rating,
        communication_rating=data.communication_rating,
        overall_rating=data.overall_rating,
        comment=data.comment,
        photo_url=data.photo_url if data.photo_consent else None,
        photo_consent=data.photo_consent,
    )

    try:
        db.add(survey)
        db.commit()
        db.refresh(survey)
        return survey
    except IntegrityError:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo guardar la evaluación o ya existe una",
        )


@router.get(
    "/me",
    response_model=list[SurveyResponse],
)
def list_my_surveys(
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_my_family(db, current_user.id)

    return db.scalars(
        select(Survey)
        .join(Registration, Survey.registration_id == Registration.id)
        .where(Registration.family_id == family.id)
        .order_by(Survey.created_at.desc())
    ).all()
