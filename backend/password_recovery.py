import hashlib
import logging
import os
import re
import secrets
from datetime import datetime, timedelta, timezone
from urllib.parse import urlencode

from fastapi import APIRouter, BackgroundTasks, Depends, Request, status
from pydantic import BaseModel, Field, field_validator
from sqlalchemy import select
from sqlalchemy.orm import Session

from database import get_db
from mailer import send_email
from models import PasswordResetToken, User
from rate_limiter import limiter


router = APIRouter(
    prefix="/auth",
    tags=["Recuperación de contraseña"],
)
logger = logging.getLogger(__name__)

RECOVERY_MESSAGE = (
    "Si existe una cuenta con ese correo, recibirás "
    "instrucciones para restablecer tu contraseña."
)


class ForgotPasswordRequest(BaseModel):
    email: str = Field(min_length=5, max_length=255)

    @field_validator("email")
    @classmethod
    def normalize_email(cls, value: str) -> str:
        value = value.strip().lower()

        if not re.fullmatch(r"[^@\s]+@[^@\s]+\.[^@\s]+", value):
            raise ValueError("Correo electrónico no válido")

        return value


class RecoveryResponse(BaseModel):
    message: str


def send_recovery_email(email: str, link: str) -> None:
    try:
        send_email(
            to=email,
            subject="Restablece tu contraseña — Familias que Suman",
            body=(
                "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                f"Abre este enlace:\n{link}\n\n"
                "El enlace vence en 20 minutos.\n"
                "Si no solicitaste este cambio, ignora este correo.\n"
                "Tu contraseña no cambia hasta que completes el proceso."
            ),
        )
    except Exception:
        # No registrar el token, el enlace ni las credenciales SMTP.
        logger.error("No se pudo enviar el correo de recuperación.")


@router.post(
    "/forgot-password",
    response_model=RecoveryResponse,
    status_code=status.HTTP_202_ACCEPTED,
)
@limiter.limit("3/minute")
def forgot_password(
    request: Request,
    payload: ForgotPasswordRequest,
    background_tasks: BackgroundTasks,
    db: Session = Depends(get_db),
):
    reset_url = os.environ["PASSWORD_RESET_URL"]

    user = db.scalar(
        select(User).where(
            User.email == payload.email,
            User.is_active.is_(True),
        )
    )

    if user is not None:
        token = secrets.token_urlsafe(32)
        token_hash = hashlib.sha256(token.encode("utf-8")).hexdigest()

        db.add(
            PasswordResetToken(
                user_id=user.id,
                token_hash=token_hash,
                expires_at=(
                    datetime.now(timezone.utc) + timedelta(minutes=20)
                ),
            )
        )
        db.commit()

        link = f"{reset_url}?{urlencode({'token': token})}"

        background_tasks.add_task(
            send_recovery_email,
            user.email,
            link,
        )

    return RecoveryResponse(message=RECOVERY_MESSAGE)