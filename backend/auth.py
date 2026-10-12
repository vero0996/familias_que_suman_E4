
import os
from datetime import datetime, timedelta, timezone

import jwt
from pwdlib import PasswordHash
from sqlalchemy import select
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from database import get_db
from models import Family, User
from fastapi import APIRouter, Depends, HTTPException, status
from dependencies import get_current_user
from schemas import (
    CurrentUserResponse,
    LoginRequest,
    RegisterRequest,
    RegisterResponse,
    TokenResponse,
)


router = APIRouter(prefix="/auth", tags=["Autenticación"])
password_hash = PasswordHash.recommended()
JWT_ALGORITHM = "HS256"
ACCESS_TOKEN_MINUTES = 60


def get_jwt_secret() -> str:
    secret = os.getenv("JWT_SECRET_KEY")
    if not secret or len(secret) < 32:
        raise HTTPException(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            detail="La autenticación no está configurada correctamente",
        )
    return secret


def create_access_token(user: User) -> str:
    now = datetime.now(timezone.utc)
    payload = {
        "sub": str(user.id),
        "role": user.role,
        "iat": now,
        "exp": now + timedelta(minutes=ACCESS_TOKEN_MINUTES),
    }

    return jwt.encode(
        payload,
        get_jwt_secret(),
        algorithm=JWT_ALGORITHM,
    )


@router.post(
    "/register",
    response_model=RegisterResponse,
    status_code=status.HTTP_201_CREATED,
)
def register(
    payload: RegisterRequest,
    db: Session = Depends(get_db),
):
    existing_user = db.scalar(
        select(User).where(User.email == payload.email)
    )

    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo registrar la cuenta con esos datos",
        )

    user = User(
        full_name=payload.full_name,
        email=payload.email,
        phone=payload.phone,
        password_hash=password_hash.hash(payload.password),
        role="familia",
        is_active=True,
    )

    try:
        db.add(user)
        db.flush()

        family = Family(
            name=payload.family_name,
            owner_user_id=user.id,
        )
        db.add(family)
        db.flush()

        result = RegisterResponse(
            id=user.id,
            full_name=user.full_name,
            email=user.email,
            phone=user.phone,
            role=user.role,
            family_id=family.id,
            family_name=family.name,
        )

        db.commit()
        return result

    except IntegrityError:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo registrar la cuenta con esos datos",
        )

    except Exception:
        db.rollback()
        raise


@router.post("/login", response_model=TokenResponse)
def login(
    payload: LoginRequest,
    db: Session = Depends(get_db),
):
    user = db.scalar(
        select(User).where(User.email == payload.email)
    )

    if (
        user is None
        or not user.is_active
        or not password_hash.verify(
            payload.password,
            user.password_hash,
        )
    ):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Correo o contraseña incorrectos",
            headers={"WWW-Authenticate": "Bearer"},
        )

    return TokenResponse(access_token=create_access_token(user))


@router.get("/me", response_model=CurrentUserResponse)
def get_me(
    current_user: User = Depends(get_current_user),
):
    return current_user