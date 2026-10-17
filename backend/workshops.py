from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from database import get_db
import models, schemas

router = APIRouter(prefix="/api/talleres", tags=["Talleres"])

@router.get("/", response_model=list[schemas.WorkshopResponse])
def get_workshops(db: Session = Depends(get_db)):
    return db.query(models.Workshop).all()

@router.post("/", response_model=schemas.WorkshopResponse)
def create_workshop(workshop: schemas.WorkshopCreate, db: Session = Depends(get_db)):
    db_workshop = models.Workshop(**workshop.model_dump())
    db.add(db_workshop)
    db.commit()
    db.refresh(db_workshop)
    return db_workshop