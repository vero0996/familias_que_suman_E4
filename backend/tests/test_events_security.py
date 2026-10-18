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
from models import Event


@pytest.fixture
def event_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    with Session(engine) as db:
        event = Event(
            title="Evento de prueba",
            description="Descripción de prueba",
            location="Monterrey",
            starts_at=datetime.now(timezone.utc) + timedelta(days=5),
        )
        db.add(event)
        db.commit()
        db.refresh(event)

        event_id = event.id

    def override_db():
        with Session(engine) as session:
            yield session

    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides.pop(get_current_user, None)

    try:
        with TestClient(app) as client:
            yield client, engine, event_id
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


def valid_event_payload():
    return {
        "title": "Nuevo evento",
        "description": "Descripción del nuevo evento",
        "location": "Monterrey",
        "starts_at": (
            datetime.now(timezone.utc) + timedelta(days=10)
        ).isoformat(),
    }


def test_events_are_public(event_sandbox):
    client, _, _ = event_sandbox

    response = client.get("/api/eventos/")

    assert response.status_code == 200
    assert len(response.json()) == 1


def test_event_can_be_read_by_id(event_sandbox):
    client, _, event_id = event_sandbox

    response = client.get(f"/api/eventos/{event_id}")

    assert response.status_code == 200
    assert response.json()["id"] == event_id


def test_family_cannot_create_event(event_sandbox):
    client, _, _ = event_sandbox
    authenticate_as("familia")

    response = client.post(
        "/api/eventos/",
        json=valid_event_payload(),
    )

    assert response.status_code == 403


def test_organization_can_create_event(event_sandbox):
    client, _, _ = event_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/eventos/",
        json=valid_event_payload(),
    )

    assert response.status_code == 201
    assert response.json()["title"] == "Nuevo evento"


def test_admin_can_create_event(event_sandbox):
    client, _, _ = event_sandbox
    authenticate_as("admin")

    response = client.post(
        "/api/eventos/",
        json=valid_event_payload(),
    )

    assert response.status_code == 201


def test_family_cannot_update_event(event_sandbox):
    client, _, event_id = event_sandbox
    authenticate_as("familia")

    response = client.patch(
        f"/api/eventos/{event_id}",
        json={"title": "Evento cambiado"},
    )

    assert response.status_code == 403


def test_organization_can_update_event(event_sandbox):
    client, _, event_id = event_sandbox
    authenticate_as("organizacion")

    response = client.patch(
        f"/api/eventos/{event_id}",
        json={"title": "Evento actualizado"},
    )

    assert response.status_code == 200
    assert response.json()["title"] == "Evento actualizado"


def test_family_cannot_delete_event(event_sandbox):
    client, _, event_id = event_sandbox
    authenticate_as("familia")

    response = client.delete(f"/api/eventos/{event_id}")

    assert response.status_code == 403


def test_organization_can_delete_event(event_sandbox):
    client, engine, event_id = event_sandbox
    authenticate_as("organizacion")

    response = client.delete(f"/api/eventos/{event_id}")

    assert response.status_code == 204

    with Session(engine) as db:
        assert db.get(Event, event_id) is None


def test_admin_can_delete_event(event_sandbox):
    client, _, event_id = event_sandbox
    authenticate_as("admin")

    response = client.delete(f"/api/eventos/{event_id}")

    assert response.status_code == 204


def test_event_not_found_returns_404(event_sandbox):
    client, _, _ = event_sandbox

    response = client.get("/api/eventos/99999")

    assert response.status_code == 404


def test_invalid_event_is_rejected(event_sandbox):
    client, _, _ = event_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/eventos/",
        json={
            "title": "",
            "description": "",
            "location": "",
            "starts_at": "fecha-invalida",
        },
    )

    assert response.status_code == 422