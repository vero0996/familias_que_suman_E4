from fastapi import APIRouter, Depends, HTTPException, status, Response
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import Donation, User
from schemas import DonationCreate, DonationResponse, DonationUpdate


router = APIRouter(
    prefix="/api/donaciones",
    tags=["Donaciones"]
)


@router.get("/", response_model=list[DonationResponse])
def get_donations(
    db: Session = Depends(get_db),
):
    return db.query(Donation).all()


@router.get("/{donation_id}", response_model=DonationResponse)
def get_donation(
    donation_id: int,
    db: Session = Depends(get_db),
):
    donation = db.get(Donation, donation_id)

    if donation is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Donación no encontrada",
        )

    return donation


@router.post(
    "/",
    response_model=DonationResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_donation(
    donation: DonationCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_role("organizacion", "admin")
    ),
):
    db_donation = Donation(
        **donation.model_dump()
    )

    db.add(db_donation)
    db.commit()
    db.refresh(db_donation)

    return db_donation


@router.patch(
    "/{donation_id}",
    response_model=DonationResponse,
)
def update_donation(
    donation_id: int,
    data: DonationUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_role("organizacion", "admin")
    ),
):
    donation = db.get(Donation, donation_id)

    if donation is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Donación no encontrada",
        )

    changes = data.model_dump(
        exclude_unset=True
    )

    for field, value in changes.items():
        setattr(donation, field, value)

    db.commit()
    db.refresh(donation)

    return donation


@router.delete(
    "/{donation_id}",
    status_code=status.HTTP_204_NO_CONTENT,
)
def delete_donation(
    donation_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(
        require_role("organizacion", "admin")
    ),
):
    donation = db.get(Donation, donation_id)

    if donation is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Donación no encontrada",
        )

    db.delete(donation)
    db.commit()

    return Response(
        status_code=status.HTTP_204_NO_CONTENT
    )