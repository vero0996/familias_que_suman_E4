from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from database import get_db
from dependencies import get_current_user
from models import Message, User
from schemas import MessageCreate, MessageResponse


router = APIRouter(
    prefix="/api/mensajes",
    tags=["Mensajes"]
)


@router.get(
    "/usuarios",
)
def get_message_users(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    role = "organizacion"

    if current_user.role == "organizacion":
        role = "familia"

    users = (
        db.query(User)
        .filter(
            User.id != current_user.id,
            User.role == role,
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
            (
                (Message.sender_user_id == current_user.id)
                & (Message.receiver_user_id == user_id)
            )
            |
            (
                (Message.sender_user_id == user_id)
                & (Message.receiver_user_id == current_user.id)
            )
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

    message = Message(
        sender_user_id=current_user.id,
        receiver_user_id=data.receiver_user_id,
        text=data.text,
    )

    db.add(message)
    db.commit()
    db.refresh(message)

    return message