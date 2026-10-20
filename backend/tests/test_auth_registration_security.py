
from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import User
from auth import password_hash
from rate_limiter import limiter


@pytest.fixture
def registration_sandbox():
    limiter.reset()
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    def override_db():
        with Session(engine) as session:
            yield session

    # El endpoint de registro es público: no necesita un usuario autenticado.
    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides.pop(get_current_user, None)

    try:
        with TestClient(app) as client:
            yield client, engine
    finally:
        app.dependency_overrides.clear()
        Base.metadata.drop_all(engine)
        engine.dispose()


@pytest.mark.parametrize("role", ["admin", "organizacion"])
def test_public_registration_cannot_assign_privileged_role(
    registration_sandbox, role
):
    client, engine = registration_sandbox

    response = client.post(
        "/auth/register",
        json={
            "full_name": "Usuario de prueba",
            "email": f"prueba-{role}@example.com",
            "password": "Password-seguro-123!",
            "family_name": "Familia de prueba",
            "role": role,
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["role"] == "familia"

    # Verifica también el rol almacenado en la base de datos.
    with Session(engine) as db:
        user = db.query(User).filter_by(
            email=f"prueba-{role}@example.com"
        ).one()

        assert user.role == "familia"


def test_registration_stores_hash_and_password_allows_login(registration_sandbox):
    client, engine = registration_sandbox
    password = "Mi clave elegida 2026!"
    email = "registro-password@example.com"

    response = client.post(
        "/auth/register",
        json={
            "full_name": "Ana Prueba",
            "family_name": "Ana Prueba",
            "email": email,
            "phone": "8112345678",
            "password": password,
        },
    )
    assert response.status_code == 201, response.text
    assert "password" not in response.json()
    assert "password_hash" not in response.json()

    with Session(engine) as db:
        user = db.query(User).filter_by(email=email).one()
        assert user.password_hash != password
        assert password_hash.verify(password, user.password_hash)

    login = client.post("/auth/login", json={"email": email, "password": password})
    assert login.status_code == 200, login.text
    profile = client.get(
        "/auth/me", headers={"Authorization": f"Bearer {login.json()['access_token']}"}
    )
    assert profile.status_code == 200
    assert profile.json()["email"] == email

    wrong_password = client.post(
        "/auth/login", json={"email": email, "password": "Otra contrasena"}
    )
    assert wrong_password.status_code == 401


@pytest.mark.parametrize("password", [None, "corta", "x" * 129])
def test_registration_rejects_missing_or_invalid_password(registration_sandbox, password):
    client, engine = registration_sandbox
    payload = {
        "full_name": "Ana Prueba",
        "family_name": "Ana Prueba",
        "email": "invalid-password@example.com",
    }
    if password is not None:
        payload["password"] = password

    response = client.post("/auth/register", json=payload)
    assert response.status_code == 422
    with Session(engine) as db:
        assert db.query(User).filter_by(email=payload["email"]).count() == 0
