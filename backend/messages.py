from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from database import get_db
from dependencies import get_current_user
from models import Activity, Event, Message, User, Workshop
from schemas import MessageCreate, MessageResponse


router = APIRouter(prefix="/api/mensajes", tags=["Mensajes"])


@router.get("/usuarios")
def get_message_users(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    if current_user.role == "familia":
        roles = ["admin"]
    elif current_user.role == "admin":
        roles = ["familia"]
    elif current_user.role == "organizacion":
        roles = ["familia"]
    else:
        roles = []

    users = (
        db.query(User)
        .filter(
            User.id != current_user.id,
            User.role.in_(roles),
            User.is_active == True,
        )
        .order_by(User.full_name)
        .all()
    )

    return [
        {
            "id": user.id,
            "full_name": user.full_name,
            "role": user.role,
        }
        for user in users
    ]


@router.get(
    "/actividad/{activity_id}/{user_id}",
    response_model=list[MessageResponse],
)
def get_activity_messages(
    activity_id: int,
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    other_user = db.get(User, user_id)

    if other_user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado",
        )

    activity = db.get(Activity, activity_id)

    if activity is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Actividad no encontrada",
        )

    messages = (
        db.query(Message)
        .filter(
            Message.activity_id == activity_id,
            Message.event_id.is_(None),
            Message.workshop_id.is_(None),
            (
                (
                    Message.sender_user_id == current_user.id
                )
                & (
                    Message.receiver_user_id == user_id
                )
            )
            |
            (
                (
                    Message.sender_user_id == user_id
                )
                & (
                    Message.receiver_user_id == current_user.id
                )
            ),
        )
        .order_by(Message.created_at)
        .all()
    )

    return messages


@router.get(
    "/evento/{event_id}/{user_id}",
    response_model=list[MessageResponse],
)
def get_event_messages(
    event_id: int,
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    other_user = db.get(User, user_id)

    if other_user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado",
        )

    event = db.get(Event, event_id)

    if event is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Evento no encontrado",
        )

    messages = (
        db.query(Message)
        .filter(
            Message.activity_id.is_(None),
            Message.event_id == event_id,
            Message.workshop_id.is_(None),
            (
                (
                    Message.sender_user_id == current_user.id
                )
                & (
                    Message.receiver_user_id == user_id
                )
            )
            |
            (
                (
                    Message.sender_user_id == user_id
                )
                & (
                    Message.receiver_user_id == current_user.id
                )
            ),
        )
        .order_by(Message.created_at)
        .all()
    )

    return messages


@router.get(
    "/taller/{workshop_id}/{user_id}",
    response_model=list[MessageResponse],
)
def get_workshop_messages(
    workshop_id: int,
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    other_user = db.get(User, user_id)

    if other_user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado",
        )

    workshop = db.get(Workshop, workshop_id)

    if workshop is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Taller no encontrado",
        )

    messages = (
        db.query(Message)
        .filter(
            Message.activity_id.is_(None),
            Message.event_id.is_(None),
            Message.workshop_id == workshop_id,
            (
                (
                    Message.sender_user_id == current_user.id
                )
                & (
                    Message.receiver_user_id == user_id
                )
            )
            |
            (
                (
                    Message.sender_user_id == user_id
                )
                & (
                    Message.receiver_user_id == current_user.id
                )
            ),
        )
        .order_by(Message.created_at)
        .all()
    )

    return messages


@router.get(
    "/{user_id}",
    response_model=list[MessageResponse],
)
def get_messages(
    user_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    other_user = db.get(User, user_id)

    if other_user is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado",
        )

    messages = (
        db.query(Message)
        .filter(
            Message.activity_id.is_(None),
            Message.event_id.is_(None),
            Message.workshop_id.is_(None),
            (
                (
                    Message.sender_user_id == current_user.id
                )
                & (
                    Message.receiver_user_id == user_id
                )
            )
            |
            (
                (
                    Message.sender_user_id == user_id
                )
                & (
                    Message.receiver_user_id == current_user.id
                )
            ),
        )
        .order_by(Message.created_at)
        .all()
    )

    return messages


@router.post(
    "/",
    response_model=MessageResponse,
    status_code=status.HTTP_201_CREATED,
)
def send_message(
    data: MessageCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    receiver = db.get(User, data.receiver_user_id)

    if receiver is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Usuario no encontrado",
        )

    contexts = [
        data.activity_id,
        data.event_id,
        data.workshop_id,
    ]

    if sum(value is not None for value in contexts) > 1:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=(
                "Un mensaje solo puede pertenecer a una actividad, "
                "un evento o un taller."
            ),
        )

    if data.activity_id is not None:
        activity = db.get(Activity, data.activity_id)

        if activity is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Actividad no encontrada",
            )

    if data.event_id is not None:
        event = db.get(Event, data.event_id)

        if event is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Evento no encontrado",
            )

    if data.workshop_id is not None:
        workshop = db.get(Workshop, data.workshop_id)

        if workshop is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Taller no encontrado",
            )

    message = Message(
        sender_user_id=current_user.id,
        receiver_user_id=data.receiver_user_id,
        activity_id=data.activity_id,
        event_id=data.event_id,
        workshop_id=data.workshop_id,
        text=data.text,
    )

    db.add(message)
    db.commit()
    db.refresh(message)

    return message