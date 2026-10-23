
from datetime import datetime, timedelta, timezone
import logging
import jwt
from pwdlib import PasswordHash
from sqlalchemy import select, func
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from database import get_db
from models import Family, FamilyMember, User
from fastapi import APIRouter, Depends, HTTPException, status, Request
from dependencies import get_current_user, get_jwt_secret
from schemas import (
    CurrentUserResponse,
    LoginRequest,
    RegisterRequest,
    RegisterResponse,
    TokenResponse,
)
from rate_limiter import limiter


router = APIRouter(prefix="/auth", tags=["Autenticación"])
logger = logging.getLogger(__name__)
password_hash = PasswordHash.recommended()
JWT_ALGORITHM = "HS256"
ACCESS_TOKEN_MINUTES = 60

def create_access_token(user: User) -> str:
    now = datetime.now(timezone.utc)
    payload = {
        "sub": str(user.id),
        "role": user.role,
        "token_version": user.token_version,
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
@limiter.limit("3/hour")
def register(
    request: Request,
    payload: RegisterRequest,
    db: Session = Depends(get_db),
):
    email_clean = payload.email.strip().lower()
    existing_user = db.scalar(
        select(User).where(func.lower(User.email) == email_clean)
    )

    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo registrar la cuenta con esos datos",
        )

    user = User(
        full_name=payload.full_name,
        email=email_clean,
        phone=payload.phone,
        password_hash=password_hash.hash(payload.password),
        role="familia",
        is_active=True,
    )

    try:
        # 1. Guardar primero al usuario y obtener su ID
        db.add(user)
        db.flush()

        # 2. Crear la familia vinculada al usuario
        family = Family(
            name=payload.family_name,
            owner_user_id=user.id,
        )
        db.add(family)
        db.flush()

        # 3. Registrar al responsable como integrante de su familia
        member = FamilyMember(
            family_id=family.id,
            full_name=user.full_name,
            age=None,
            relationship_type="responsable",
            is_active=True,
        )
        db.add(member)

        # 4. Preparar la respuesta
        result = RegisterResponse(
            id=user.id,
            full_name=user.full_name,
            email=user.email,
            phone=user.phone,
            role=user.role,
            family_id=family.id,
            family_name=family.name,
        )

        # 5. Confirmar todos los cambios juntos
        db.commit()
        return result

    except IntegrityError as exc:
        db.rollback()

        original_error = exc.orig
        diagnostic = getattr(original_error, "diag", None)

        logger.error(
            "Error de integridad al registrar. "
            "Tipo=%s; tabla=%s; columna=%s; restricción=%s",
            type(original_error).__name__,
            getattr(diagnostic, "table_name", None),
            getattr(diagnostic, "column_name", None),
            getattr(diagnostic, "constraint_name", None),
        )

        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="No se pudo registrar la cuenta con esos datos",
        )

    except Exception:
        db.rollback()
        raise



@router.post("/login", response_model=TokenResponse)
@limiter.limit("5/minute")
def login(
    request: Request,
    payload: LoginRequest,
    db: Session = Depends(get_db),
):
    email_clean = payload.email.strip().lower()
    user = db.scalar(
        select(User).where(func.lower(User.email) == email_clean)
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