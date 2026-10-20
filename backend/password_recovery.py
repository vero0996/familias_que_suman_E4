import hashlib
import logging
import os
import re
import secrets
from datetime import datetime, timedelta, timezone
from urllib.parse import urlencode

from fastapi import (
    APIRouter, BackgroundTasks, Depends, HTTPException, Request, status
)
from pydantic import BaseModel, Field, field_validator, model_validator
from sqlalchemy import select, update
from sqlalchemy.orm import Session
from auth import password_hash

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

class ResetPasswordRequest(BaseModel):
    token: str = Field(min_length=43, max_length=128)
    new_password: str = Field(min_length=8, max_length=128, repr=False)
    confirm_password: str = Field(min_length=8, max_length=128, repr=False)

    @model_validator(mode="after")
    def passwords_match(self):
        if self.new_password != self.confirm_password:
            raise ValueError("Las contraseñas no coinciden")
        return self


@router.post(
    "/reset-password",
    response_model=RecoveryResponse,
)
@limiter.limit("5/minute")
def reset_password(
    request: Request,
    payload: ResetPasswordRequest,
    db: Session = Depends(get_db),
):
    invalid_link = "El enlace no es válido o ya venció. Solicita uno nuevo."

    token_hash = hashlib.sha256(
        payload.token.encode("utf-8")
    ).hexdigest()

    user_id = db.scalar(
        select(PasswordResetToken.user_id).where(
            PasswordResetToken.token_hash == token_hash
        )
    )

    if user_id is None:
        raise HTTPException(status_code=400, detail=invalid_link)

    # Serializa los cambios de contraseña para esta cuenta.
    user = db.scalar(
        select(User)
        .where(User.id == user_id, User.is_active.is_(True))
        .with_for_update()
    )

    if user is None:
        raise HTTPException(status_code=400, detail=invalid_link)

    now = datetime.now(timezone.utc)

    reset_token = db.scalar(
        select(PasswordResetToken).where(
            PasswordResetToken.token_hash == token_hash,
            PasswordResetToken.user_id == user.id,
            PasswordResetToken.used_at.is_(None),
            PasswordResetToken.expires_at > now,
        )
    )

    if reset_token is None:
        raise HTTPException(status_code=400, detail=invalid_link)

    user.password_hash = password_hash.hash(payload.new_password)

    # Invalida todos los enlaces pendientes de esta cuenta.
    db.execute(
        update(PasswordResetToken)
        .where(
            PasswordResetToken.user_id == user.id,
            PasswordResetToken.used_at.is_(None),
        )
        .values(used_at=now)
        .execution_options(synchronize_session=False)
    )

    # Guarda la contraseña y el uso de los tokens juntos.
    db.commit()

    return RecoveryResponse(
        message="Contraseña actualizada. Ya puedes iniciar sesión."
    )