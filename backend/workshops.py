from fastapi import APIRouter, Depends, HTTPException, status, Response
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import Workshop, User
from schemas import WorkshopCreate, WorkshopResponse, WorkshopUpdate


router = APIRouter(prefix="/api/talleres", tags=["Talleres"])


@router.get("/", response_model=list[WorkshopResponse])
def get_workshops(db: Session = Depends(get_db)):
    return db.query(Workshop).all()


@router.get("/{workshop_id}", response_model=WorkshopResponse)
def get_workshop(workshop_id: int, db: Session = Depends(get_db)):
    workshop = db.get(Workshop, workshop_id)

    if workshop is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Taller no encontrado",
        )

    return workshop


@router.post(
    "/",
    response_model=WorkshopResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_workshop(
    workshop: WorkshopCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    db_workshop = Workshop(**workshop.model_dump())

    db.add(db_workshop)
    db.commit()
    db.refresh(db_workshop)

    return db_workshop


@router.patch("/{workshop_id}", response_model=WorkshopResponse)
def update_workshop(
    workshop_id: int,
    data: WorkshopUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    workshop = db.get(Workshop, workshop_id)

    if workshop is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Taller no encontrado",
        )

    changes = data.model_dump(exclude_unset=True, exclude_none=True)

    for field, value in changes.items():
        setattr(workshop, field, value)

    db.commit()
    db.refresh(workshop)

    return workshop


@router.delete("/{workshop_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_workshop(
    workshop_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    workshop = db.get(Workshop, workshop_id)

    if workshop is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Taller no encontrado",
        )

    db.delete(workshop)
    db.commit()

    return Response(status_code=status.HTTP_204_NO_CONTENT)