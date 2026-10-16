import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from main import app
from models import User


@pytest.fixture
def login_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    def override_db():
        with Session(engine) as session:
            yield session

    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides[get_db] = override_db

    try:
        with TestClient(app) as client:
            yield client, engine
    finally:
        app.dependency_overrides.clear()
        app.dependency_overrides.update(previous_overrides)
        Base.metadata.drop_all(engine)
        engine.dispose()


def test_login_unknown_email_returns_generic_error(login_sandbox):
    client, _ = login_sandbox

    response = client.post(
        "/auth/login",
        json={
            "email": "no-existe@example.com",
            "password": "Password-seguro-123!",
        },
    )

    assert response.status_code == 401
    assert response.json()["detail"] == "Correo o contraseña incorrectos"
    assert "access_token" not in response.json()

def test_login_wrong_password_returns_generic_error(login_sandbox):
    client, engine = login_sandbox

    from auth import password_hash

    with Session(engine) as db:
        db.add(
            User(
                full_name="Usuario de prueba",
                email="usuario@example.com",
                phone=None,
                password_hash=password_hash.hash("Otra-contraseña-123!"),
                role="familia",
                is_active=True,
            )
        )
        db.commit()

    response = client.post(
        "/auth/login",
        json={
            "email": "usuario@example.com",
            "password": "Password-incorrecta-123!",
        },
    )

    assert response.status_code == 401
    assert response.json()["detail"] == "Correo o contraseña incorrectos"
    assert "access_token" not in response.json()

def test_login_inactive_account_is_rejected(login_sandbox):
    client, engine = login_sandbox

    from auth import password_hash

    with Session(engine) as db:
        db.add(
            User(
                full_name="Usuario desactivado",
                email="inactivo@example.com",
                phone=None,
                password_hash=password_hash.hash("Password-seguro-123!"),
                role="familia",
                is_active=False,
            )
        )
        db.commit()

    response = client.post(
        "/auth/login",
        json={
            "email": "inactivo@example.com",
            "password": "Password-seguro-123!",
        },
    )

    assert response.status_code == 401
    assert response.json()["detail"] == "Correo o contraseña incorrectos"
    assert "access_token" not in response.json()


def test_login_response_does_not_expose_sensitive_user_fields(login_sandbox):
    client, engine = login_sandbox

    from auth import password_hash

    password = "Password-seguro-123!"

    with Session(engine) as db:
        db.add(
            User(
                full_name="Usuario de prueba",
                email="respuesta-segura@example.com",
                phone=None,
                password_hash=password_hash.hash(password),
                role="familia",
                is_active=True,
            )
        )
        db.commit()

    response = client.post(
        "/auth/login",
        json={
            "email": "respuesta-segura@example.com",
            "password": password,
        },
    )

    assert response.status_code == 200, response.text

    body = response.json()
    assert set(body) == {"access_token", "token_type"}
    assert "password" not in body
    assert "password_hash" not in body
    assert "email" not in body


def test_current_user_response_does_not_expose_sensitive_fields(login_sandbox):
    client, engine = login_sandbox

    from auth import create_access_token, password_hash

    with Session(engine) as db:
        user = User(
            full_name="Usuario de prueba",
            email="perfil-seguro@example.com",
            phone=None,
            password_hash=password_hash.hash("Password-seguro-123!"),
            role="familia",
            is_active=True,
        )
        db.add(user)
        db.commit()
        db.refresh(user)
        token = create_access_token(user)

    response = client.get(
        "/auth/me",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 200, response.text

    body = response.json()
    assert set(body) == {"id", "full_name", "email", "phone", "role"}
    assert "password" not in body
    assert "password_hash" not in body
    assert "is_active" not in body


def test_inactive_user_cannot_use_existing_token(login_sandbox):
    client, engine = login_sandbox

    from auth import create_access_token, password_hash

    with Session(engine) as db:
        user = User(
            full_name="Usuario desactivado después del login",
            email="token-inactivo@example.com",
            phone=None,
            password_hash=password_hash.hash("Password-seguro-123!"),
            role="familia",
            is_active=True,
        )
        db.add(user)
        db.commit()
        db.refresh(user)

        token = create_access_token(user)
        user_id = user.id

    # Simula la desactivación posterior al inicio de sesión.
    with Session(engine) as db:
        user = db.get(User, user_id)
        user.is_active = False
        db.commit()

    response = client.get(
        "/auth/me",
        headers={"Authorization": f"Bearer {token}"},
    )

    assert response.status_code == 401, response.text
    assert response.json()["detail"] == "Usuario no disponible"
