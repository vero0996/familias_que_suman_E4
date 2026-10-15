
from datetime import datetime, timedelta, timezone
from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session, sessionmaker
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Activity, Campaign, Family, Registration, Survey, User


@pytest.fixture
def survey_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    TestSession = sessionmaker(
        bind=engine,
        autoflush=False,
        expire_on_commit=False,
    )
    Base.metadata.create_all(bind=engine)

    with TestSession() as db:
        user_a = User(
            full_name="Familia A",
            email="familia-a-survey@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        user_b = User(
            full_name="Familia B",
            email="familia-b-survey@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        db.add_all([user_a, user_b])
        db.flush()

        family_a = Family(
            name="Familia A",
            owner_user_id=user_a.id,
        )
        family_b = Family(
            name="Familia B",
            owner_user_id=user_b.id,
        )
        campaign = Campaign(
            title="Campaña de prueba",
            description="Datos ficticios",
            organization_name="Organización de prueba",
            status="publicada",
        )
        db.add_all([family_a, family_b, campaign])
        db.flush()

        now = datetime.now(timezone.utc)
        activity_a = Activity(
            campaign_id=campaign.id,
            title="Actividad A",
            description="Actividad finalizada de prueba",
            starts_at=now - timedelta(days=5),
            ends_at=now - timedelta(days=5, hours=-2),
            location="Lugar de prueba",
            capacity_people=10,
            registration_deadline=now - timedelta(days=6),
            status="publicada",
        )
        activity_b = Activity(
            campaign_id=campaign.id,
            title="Actividad B",
            description="Actividad finalizada de prueba",
            starts_at=now - timedelta(days=4),
            ends_at=now - timedelta(days=4, hours=-2),
            location="Lugar de prueba",
            capacity_people=10,
            registration_deadline=now - timedelta(days=5),
            status="publicada",
        )
        db.add_all([activity_a, activity_b])
        db.flush()

        registration_a = Registration(
            activity_id=activity_a.id,
            family_id=family_a.id,
            status="confirmada",
        )
        registration_b = Registration(
            activity_id=activity_b.id,
            family_id=family_b.id,
            status="confirmada",
        )
        db.add_all([registration_a, registration_b])
        db.commit()

        ids = {
            "user_a": user_a.id,
            "registration_a": registration_a.id,
            "registration_b": registration_b.id,
        }

    def override_db():
        with TestSession() as db:
            yield db

    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=ids["user_a"],
        role="familia",
        is_active=True,
    )

    try:
        with TestClient(app) as client:
            yield client, TestSession, ids
    finally:
        app.dependency_overrides.clear()
        Base.metadata.drop_all(bind=engine)
        engine.dispose()


def survey_payload(registration_id):
    return {
        "registration_id": registration_id,
        "campaign_rating": 5,
        "organization_rating": 4,
        "communication_rating": 5,
        "overall_rating": 5,
        "comment": "  Buena experiencia  ",
        "photo_url": "https://example.com/foto.jpg",
        "photo_consent": False,
    }


def test_family_cannot_evaluate_foreign_registration(survey_sandbox):
    client, TestSession, ids = survey_sandbox

    response = client.post(
        "/surveys",
        json=survey_payload(ids["registration_b"]),
    )

    assert response.status_code == 403

    with TestSession() as db:
        assert db.query(Survey).count() == 0


def test_family_can_create_survey_for_own_registration(survey_sandbox):
    client, _, ids = survey_sandbox

    response = client.post(
        "/surveys",
        json=survey_payload(ids["registration_a"]),
    )

    assert response.status_code == 201, response.text
    data = response.json()

    assert data["registration_id"] == ids["registration_a"]
    assert data["comment"] == "Buena experiencia"
    assert data["photo_consent"] is False
    assert data["photo_url"] is None


def test_family_cannot_submit_duplicate_survey(survey_sandbox):
    client, _, ids = survey_sandbox
    payload = survey_payload(ids["registration_a"])

    first_response = client.post("/surveys", json=payload)
    assert first_response.status_code == 201, first_response.text

    second_response = client.post("/surveys", json=payload)
    assert second_response.status_code == 409


def test_family_can_only_list_own_surveys(survey_sandbox):
    client, TestSession, ids = survey_sandbox

    with TestSession() as db:
        db.add_all([
            Survey(
                registration_id=ids["registration_a"],
                campaign_rating=5,
                organization_rating=5,
                communication_rating=5,
                overall_rating=5,
                comment="Encuesta propia",
                photo_consent=False,
            ),
            Survey(
                registration_id=ids["registration_b"],
                campaign_rating=4,
                organization_rating=4,
                communication_rating=4,
                overall_rating=4,
                comment="Encuesta de otra familia",
                photo_consent=False,
            ),
        ])
        db.commit()

    response = client.get("/surveys/me")

    assert response.status_code == 200
    data = response.json()

    assert len(data) == 1
    assert data[0]["registration_id"] == ids["registration_a"]
    assert data[0]["comment"] == "Encuesta propia"


def test_survey_rejects_rating_above_five(survey_sandbox):
    client, _, ids = survey_sandbox
    payload = survey_payload(ids["registration_a"])
    payload["overall_rating"] = 6

    response = client.post("/surveys", json=payload)

    assert response.status_code == 422


def test_survey_rejects_rating_below_one(survey_sandbox):
    client, _, ids = survey_sandbox
    payload = survey_payload(ids["registration_a"])
    payload["campaign_rating"] = 0

    response = client.post("/surveys", json=payload)

    assert response.status_code == 422
