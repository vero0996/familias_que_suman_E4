from fastapi import APIRouter, Depends, HTTPException, status, Response
from sqlalchemy.orm import Session

from database import get_db
from dependencies import require_role
from models import Event, User
from schemas import EventCreate, EventResponse, EventUpdate


router = APIRouter(prefix="/api/eventos", tags=["Eventos"])


@router.get("/", response_model=list[EventResponse])
def get_events(db: Session = Depends(get_db)):
    return db.query(Event).all()


@router.get("/{event_id}", response_model=EventResponse)
def get_event(event_id: int, db: Session = Depends(get_db)):
    event = db.get(Event, event_id)

    if event is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Evento no encontrado",
        )

    return event


@router.post(
    "/",
    response_model=EventResponse,
    status_code=status.HTTP_201_CREATED,
)
def create_event(
    event: EventCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    db_event = Event(**event.model_dump())

    db.add(db_event)
    db.commit()
    db.refresh(db_event)

    return db_event


@router.patch("/{event_id}", response_model=EventResponse)
def update_event(
    event_id: int,
    data: EventUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    event = db.get(Event, event_id)

    if event is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Evento no encontrado",
        )

    changes = data.model_dump(exclude_unset=True, exclude_none=True)

    for field, value in changes.items():
        setattr(event, field, value)

    db.commit()
    db.refresh(event)

    return event


@router.delete("/{event_id}", status_code=status.HTTP_204_NO_CONTENT)
def delete_event(
    event_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(require_role("organizacion", "admin")),
):
    event = db.get(Event, event_id)

    if event is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Evento no encontrado",
        )

    db.delete(event)
    db.commit()

    return Response(status_code=status.HTTP_204_NO_CONTENT)