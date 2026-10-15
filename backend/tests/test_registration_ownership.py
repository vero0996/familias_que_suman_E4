
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
from models import Activity, Campaign, Family, FamilyMember, Registration, User


@pytest.fixture
def registration_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    with Session(engine) as db:
        family_user = User(
            full_name="Familia A",
            email="familia-a@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        other_user = User(
            full_name="Familia B",
            email="familia-b@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        db.add_all([family_user, other_user])
        db.flush()

        family_a = Family(
            name="Familia A",
            owner_user_id=family_user.id,
        )
        family_b = Family(
            name="Familia B",
            owner_user_id=other_user.id,
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
            title="Actividad propia",
            description="Prueba",
            starts_at=now + timedelta(days=10),
            ends_at=now + timedelta(days=10, hours=2),
            location="Lugar de prueba",
            capacity_people=10,
            registration_deadline=now + timedelta(days=5),
            status="publicada",
        )
        activity_b = Activity(
            campaign_id=campaign.id,
            title="Actividad de prueba",
            description="Prueba",
            starts_at=now + timedelta(days=10),
            ends_at=now + timedelta(days=10, hours=2),
            location="Lugar de prueba",
            capacity_people=10,
            registration_deadline=now + timedelta(days=5),
            status="publicada",
        )
        db.add_all([activity_a, activity_b])
        db.flush()

        own_member = FamilyMember(
            family_id=family_a.id,
            full_name="Integrante propio",
            age=12,
            relationship_type="Hijo",
            is_active=True,
        )
        foreign_member = FamilyMember(
            family_id=family_b.id,
            full_name="Integrante ajeno",
            age=10,
            relationship_type="Hijo",
            is_active=True,
        )
        inactive_member = FamilyMember(
            family_id=family_a.id,
            full_name="Integrante inactivo",
            age=15,
            relationship_type="Hijo",
            is_active=False,
        )
        db.add_all([own_member, foreign_member, inactive_member])
        db.flush()

        own_member_id = own_member.id
        foreign_member_id = foreign_member.id
        inactive_member_id = inactive_member.id
        target_activity_id = activity_b.id

        own_registration = Registration(
            activity_id=activity_a.id,
            family_id=family_a.id,
            status="confirmada",
        )
        foreign_registration = Registration(
            activity_id=activity_b.id,
            family_id=family_b.id,
            status="confirmada",
        )
        db.add_all([own_registration, foreign_registration])
        db.commit()

        own_id = own_registration.id
        foreign_id = foreign_registration.id
        family_a_user_id = family_user.id

    def override_db():
        with Session(engine) as session:
            yield session

    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=family_a_user_id,
        role="familia",
        is_active=True,
    )

    try:
        with TestClient(app) as client:
            yield (
                client,
                engine,
                own_id,
                foreign_id,
                target_activity_id,
                own_member_id,
                foreign_member_id,
                inactive_member_id,
            )
    finally:
        app.dependency_overrides.clear()
        Base.metadata.drop_all(engine)
        engine.dispose()


def test_family_can_read_own_registration(registration_sandbox):
    client, _, own_id, _, *_ = registration_sandbox

    response = client.get(f"/registrations/{own_id}")

    assert response.status_code == 200
    assert response.json()["id"] == own_id


def test_family_cannot_read_foreign_registration(registration_sandbox):
    client, _, _, foreign_id, *_ = registration_sandbox

    response = client.get(f"/registrations/{foreign_id}")

    assert response.status_code == 403


def test_family_cannot_cancel_foreign_registration(registration_sandbox):
    client, engine, _, foreign_id, *_ = registration_sandbox

    response = client.patch(
        f"/registrations/{foreign_id}/cancel",
        json={"reason": "Intento de prueba"},
    )

    assert response.status_code == 403

    with Session(engine) as db:
        registration = db.get(Registration, foreign_id)

        assert registration is not None
        assert registration.status == "confirmada"
        assert registration.canceled_at is None


def test_missing_registration_returns_404(registration_sandbox):
    client, *_ = registration_sandbox

    response = client.get("/registrations/999999")

    assert response.status_code == 404


def test_family_can_register_own_active_member(registration_sandbox):
    client, _, _, _, activity_id, own_member_id, *_ = registration_sandbox

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text


def test_family_cannot_register_foreign_member(registration_sandbox):
    client, _, _, _, activity_id, _, foreign_member_id, *_ = registration_sandbox

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [foreign_member_id],
        },
    )

    assert response.status_code == 422


def test_family_cannot_register_nonexistent_member(registration_sandbox):
    client, _, _, _, activity_id, *_ = registration_sandbox

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [999999],
        },
    )

    assert response.status_code == 422


def test_family_cannot_register_inactive_member(registration_sandbox):
    client, _, _, _, activity_id, _, _, inactive_member_id = registration_sandbox

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [inactive_member_id],
        },
    )

    assert response.status_code == 422

def test_family_cannot_create_duplicate_active_registration(registration_sandbox):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        existing = db.get(Registration, own_id)
        activity_id = existing.activity_id

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text