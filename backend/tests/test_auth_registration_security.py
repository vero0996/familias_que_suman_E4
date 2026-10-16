
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


@pytest.fixture
def registration_sandbox():
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
