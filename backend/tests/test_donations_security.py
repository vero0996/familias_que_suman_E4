from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Donation


@pytest.fixture
def donation_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )

    Base.metadata.create_all(engine)

    def override_db():
        with Session(engine) as db:
            yield db

    previous_overrides = app.dependency_overrides.copy()

    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides.pop(get_current_user, None)

    try:
        with TestClient(app) as client:
            yield client, engine
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


def create_donation(engine):
    with Session(engine) as db:
        donation = Donation(
            title="Donación de prueba",
            description="Descripción de prueba",
            goal_amount=10000,
            current_amount=2500,
        )

        db.add(donation)
        db.commit()
        db.refresh(donation)

        return donation.id


def test_donations_are_public(donation_sandbox):
    client, engine = donation_sandbox

    create_donation(engine)

    response = client.get("/api/donaciones/")

    assert response.status_code == 200
    assert len(response.json()) == 1


def test_donation_detail_is_public(donation_sandbox):
    client, engine = donation_sandbox

    donation_id = create_donation(engine)

    response = client.get(
        f"/api/donaciones/{donation_id}"
    )

    assert response.status_code == 200
    assert response.json()["title"] == "Donación de prueba"


def test_missing_donation_returns_404(donation_sandbox):
    client, _ = donation_sandbox

    response = client.get(
        "/api/donaciones/999"
    )

    assert response.status_code == 404


def test_family_cannot_create_donation(donation_sandbox):
    client, _ = donation_sandbox

    authenticate_as("familia")

    response = client.post(
        "/api/donaciones/",
        json={
            "title": "Nueva donación",
            "description": "Descripción de prueba",
            "goal_amount": 5000,
            "current_amount": 0,
        },
    )

    assert response.status_code == 403


def test_organization_can_create_donation(donation_sandbox):
    client, _ = donation_sandbox

    authenticate_as("organizacion")

    response = client.post(
        "/api/donaciones/",
        json={
            "title": "Nueva donación",
            "description": "Descripción de prueba",
            "goal_amount": 5000,
            "current_amount": 0,
        },
    )

    assert response.status_code == 201
    assert response.json()["title"] == "Nueva donación"


def test_admin_can_create_donation(donation_sandbox):
    client, _ = donation_sandbox

    authenticate_as("admin")

    response = client.post(
        "/api/donaciones/",
        json={
            "title": "Donación administrativa",
            "description": "Descripción de prueba",
            "goal_amount": 8000,
            "current_amount": 1000,
        },
    )

    assert response.status_code == 201


def test_invalid_goal_amount_is_rejected(donation_sandbox):
    client, _ = donation_sandbox

    authenticate_as("organizacion")

    response = client.post(
        "/api/donaciones/",
        json={
            "title": "Nueva donación",
            "description": "Descripción de prueba",
            "goal_amount": 0,
            "current_amount": 0,
        },
    )

    assert response.status_code == 422


def test_family_cannot_update_donation(donation_sandbox):
    client, engine = donation_sandbox

    donation_id = create_donation(engine)

    authenticate_as("familia")

    response = client.patch(
        f"/api/donaciones/{donation_id}",
        json={
            "current_amount": 3000,
        },
    )

    assert response.status_code == 403


def test_organization_can_update_donation(donation_sandbox):
    client, engine = donation_sandbox

    donation_id = create_donation(engine)

    authenticate_as("organizacion")

    response = client.patch(
        f"/api/donaciones/{donation_id}",
        json={
            "current_amount": 4000,
        },
    )

    assert response.status_code == 200
    assert response.json()["current_amount"] == 4000


def test_family_cannot_delete_donation(donation_sandbox):
    client, engine = donation_sandbox

    donation_id = create_donation(engine)

    authenticate_as("familia")

    response = client.delete(
        f"/api/donaciones/{donation_id}"
    )

    assert response.status_code == 403


def test_admin_can_delete_donation(donation_sandbox):
    client, engine = donation_sandbox

    donation_id = create_donation(engine)

    authenticate_as("admin")

    response = client.delete(
        f"/api/donaciones/{donation_id}"
    )

    assert response.status_code == 204


def test_update_missing_donation_returns_404(donation_sandbox):
    client, _ = donation_sandbox

    authenticate_as("organizacion")

    response = client.patch(
        "/api/donaciones/999",
        json={
            "current_amount": 2000,
        },
    )

    assert response.status_code == 404