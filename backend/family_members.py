from datetime import date

from fastapi import APIRouter, Depends, HTTPException, status, Response
from pydantic import BaseModel, ConfigDict, Field, field_validator, model_validator
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


def calculate_age(date_of_birth: date) -> int:
    today = date.today()
    return (
        today.year
        - date_of_birth.year
        - ((today.month, today.day) < (date_of_birth.month, date_of_birth.day))
    )


class FamilyMemberCreate(BaseModel):
    full_name: str = Field(min_length=2, max_length=150)
    date_of_birth: date | None = None
    age: int | None = Field(default=None, ge=0, le=120)
    relationship_type: str = Field(min_length=2, max_length=50)

    @field_validator("full_name", "relationship_type")
    @classmethod
    def clean_text(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value

    @model_validator(mode="after")
    def validate_birth_date(self):
        if self.date_of_birth is not None:
            if self.date_of_birth > date.today():
                raise ValueError("La fecha de nacimiento no puede estar en el futuro")

            calculated_age = calculate_age(self.date_of_birth)
            if calculated_age > 120:
                raise ValueError("La fecha de nacimiento no es válida")

            self.age = calculated_age

        return self

class FamilyMemberUpdate(BaseModel):
    full_name: str = Field(min_length=2, max_length=150)
    date_of_birth: date
    relationship_type: str = Field(min_length=2, max_length=50)

    @field_validator("full_name", "relationship_type")
    @classmethod
    def clean_text(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value

    @field_validator("date_of_birth")
    @classmethod
    def validate_birth_date(cls, value: date) -> date:
        if value > date.today():
            raise ValueError(
                "La fecha de nacimiento no puede estar en el futuro"
            )

        if calculate_age(value) > 120:
            raise ValueError("La fecha de nacimiento no es válida")

        return value

class FamilyMemberResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    family_id: int
    full_name: str
    date_of_birth: date | None
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
        date_of_birth=data.date_of_birth,
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



@router.delete("/{member_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_family_member(
    member_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_my_family(db, current_user.id)

    member = db.scalar(
        select(FamilyMember).where(
            FamilyMember.id == member_id,
            FamilyMember.family_id == family.id,
            FamilyMember.is_active.is_(True),
        )
    )

    if member is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No se encontró el integrante activo de tu familia.",
        )

    member.is_active = False
    db.commit()

    return Response(status_code=status.HTTP_204_NO_CONTENT)

@router.put(
    "/{member_id}",
    response_model=FamilyMemberResponse,
)
def update_family_member(
    member_id: int,
    data: FamilyMemberUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("familia")),
):
    family = get_my_family(db, current_user.id)

    member = db.scalar(
        select(FamilyMember).where(
            FamilyMember.id == member_id,
            FamilyMember.family_id == family.id,
            FamilyMember.is_active.is_(True),
        )
    )

    if member is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No se encontró el integrante activo de tu familia.",
        )

    member.full_name = data.full_name
    member.date_of_birth = data.date_of_birth
    member.age = calculate_age(data.date_of_birth)
    member.relationship_type = data.relationship_type

    try:
        db.commit()
        db.refresh(member)
        return member
    except IntegrityError:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudieron guardar los cambios del integrante.",
        )