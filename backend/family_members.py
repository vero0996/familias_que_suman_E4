from fastapi import APIRouter, Depends, HTTPException, status
from pydantic import BaseModel, ConfigDict, Field, field_validator
from sqlalchemy import select
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import Family, FamilyMember, User


router = APIRouter(
    prefix="/family-members",
    tags=["Integrantes de familia"],
)


class FamilyMemberCreate(BaseModel):
    full_name: str = Field(min_length=2, max_length=150)
    age: int | None = Field(default=None, ge=0, le=120)
    relationship_type: str = Field(min_length=2, max_length=50)

    @field_validator("full_name", "relationship_type")
    @classmethod
    def clean_text(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value


class FamilyMemberResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    family_id: int
    full_name: str
    age: int | None
    relationship_type: str
    is_active: bool


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
    response_model=FamilyMemberResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_family_member(
    data: FamilyMemberCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_my_family(db, current_user.id)

    member = FamilyMember(
        family_id=family.id,
        full_name=data.full_name,
        age=data.age,
        relationship_type=data.relationship_type,
        is_active=True,
    )

    try:
        db.add(member)
        db.commit()
        db.refresh(member)
        return member
    except IntegrityError:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo registrar al integrante",
        )


@router.get(
    "/me",
    response_model=list[FamilyMemberResponse],
)
def list_my_family_members(
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_my_family(db, current_user.id)

    return db.scalars(
        select(FamilyMember)
        .where(FamilyMember.family_id == family.id)
        .order_by(FamilyMember.id)
    ).all()
