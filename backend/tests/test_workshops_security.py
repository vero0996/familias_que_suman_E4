from datetime import datetime, timedelta, timezone
from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Workshop


@pytest.fixture
def workshop_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    with Session(engine) as db:
        workshop = Workshop(
            title="Taller de prueba",
            description="Descripción de prueba",
            location="Monterrey",
            starts_at=datetime.now(timezone.utc) + timedelta(days=5),
        )
        db.add(workshop)
        db.commit()
        db.refresh(workshop)

        workshop_id = workshop.id

    def override_db():
        with Session(engine) as session:
            yield session

    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides.pop(get_current_user, None)

    try:
        with TestClient(app) as client:
            yield client, engine, workshop_id
    finally:
        app.dependency_overrides.clear()
        app.dependency_overrides.update(previous_overrides)
        Base.metadata.drop_all(engine)
        engine.dispose()


def authenticate_as(role: str):
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=1,
        role=role,
        is_active=True,
    )


def valid_workshop_payload():
    return {
        "title": "Nuevo taller",
        "description": "Descripción del nuevo taller",
        "location": "Monterrey",
        "starts_at": (
            datetime.now(timezone.utc) + timedelta(days=10)
        ).isoformat(),
    }


def test_workshops_are_public(workshop_sandbox):
    client, _, _ = workshop_sandbox

    response = client.get("/api/talleres/")

    assert response.status_code == 200
    assert len(response.json()) == 1


def test_workshop_can_be_read_by_id(workshop_sandbox):
    client, _, workshop_id = workshop_sandbox

    response = client.get(f"/api/talleres/{workshop_id}")

    assert response.status_code == 200
    assert response.json()["id"] == workshop_id


def test_family_cannot_create_workshop(workshop_sandbox):
    client, _, _ = workshop_sandbox
    authenticate_as("familia")

    response = client.post(
        "/api/talleres/",
        json=valid_workshop_payload(),
    )

    assert response.status_code == 403


def test_organization_can_create_workshop(workshop_sandbox):
    client, _, _ = workshop_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/talleres/",
        json=valid_workshop_payload(),
    )

    assert response.status_code == 201
    assert response.json()["title"] == "Nuevo taller"


def test_admin_can_create_workshop(workshop_sandbox):
    client, _, _ = workshop_sandbox
    authenticate_as("admin")

    response = client.post(
        "/api/talleres/",
        json=valid_workshop_payload(),
    )

    assert response.status_code == 201


def test_family_cannot_update_workshop(workshop_sandbox):
    client, _, workshop_id = workshop_sandbox
    authenticate_as("familia")

    response = client.patch(
        f"/api/talleres/{workshop_id}",
        json={"title": "Taller cambiado"},
    )

    assert response.status_code == 403


def test_organization_can_update_workshop(workshop_sandbox):
    client, _, workshop_id = workshop_sandbox
    authenticate_as("organizacion")

    response = client.patch(
        f"/api/talleres/{workshop_id}",
        json={"title": "Taller actualizado"},
    )

    assert response.status_code == 200
    assert response.json()["title"] == "Taller actualizado"


def test_family_cannot_delete_workshop(workshop_sandbox):
    client, _, workshop_id = workshop_sandbox
    authenticate_as("familia")

    response = client.delete(f"/api/talleres/{workshop_id}")

    assert response.status_code == 403


def test_organization_can_delete_workshop(workshop_sandbox):
    client, engine, workshop_id = workshop_sandbox
    authenticate_as("organizacion")

    response = client.delete(f"/api/talleres/{workshop_id}")

    assert response.status_code == 204

    with Session(engine) as db:
        assert db.get(Workshop, workshop_id) is None


def test_admin_can_delete_workshop(workshop_sandbox):
    client, _, workshop_id = workshop_sandbox
    authenticate_as("admin")

    response = client.delete(f"/api/talleres/{workshop_id}")

    assert response.status_code == 204


def test_workshop_not_found_returns_404(workshop_sandbox):
    client, _, _ = workshop_sandbox

    response = client.get("/api/talleres/99999")

    assert response.status_code == 404


def test_invalid_workshop_is_rejected(workshop_sandbox):
    client, _, _ = workshop_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/talleres/",
        json={
            "title": "",
            "description": "",
            "location": "",
            "starts_at": "fecha-invalida",
        },
    )

    assert response.status_code == 422