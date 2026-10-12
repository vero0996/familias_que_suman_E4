
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import User
from schemas import (
    CurrentUserResponse,
    OrganizationAdminResponse,
    OrganizationPromoteRequest,
)

router = APIRouter(prefix="/admin", tags=["Administración"])


@router.get(
    "/organizations",
    response_model=list[OrganizationAdminResponse],
)
def list_organizations(
    db: Session = Depends(get_db),
    admin: User = Depends(require_role("admin")),
):
    """Lista las cuentas con rol de organización."""
    statement = (
        select(User)
        .where(User.role == "organizacion")
        .order_by(User.full_name)
    )
    return db.scalars(statement).all()


@router.post(
    "/organizations",
    response_model=OrganizationAdminResponse,
    status_code=status.HTTP_200_OK,
)
def promote_organization(
    data: OrganizationPromoteRequest,
    db: Session = Depends(get_db),
    admin: User = Depends(require_role("admin")),
):
    """Asigna el rol organización a una cuenta existente."""
    user = db.scalar(
        select(User).where(User.email == data.email)
    )

    if user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No existe una cuenta con ese correo",
        )

    if not user.is_active:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se puede asignar una organización a una cuenta desactivada",
        )

    if user.role == "admin":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se puede cambiar el rol de una cuenta administradora",
        )

    if user.role == "organizacion":
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="La cuenta ya tiene el rol de organización",
        )

    user.role = "organizacion"
    db.commit()
    db.refresh(user)

    return user
