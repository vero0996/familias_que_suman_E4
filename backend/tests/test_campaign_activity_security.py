
from datetime import datetime, timedelta, timezone
from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Activity, Campaign, User


@pytest.fixture
def security_sandbox():
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

    now = datetime.now(timezone.utc)

    with TestSession() as db:
        org_a = User(
            full_name="Organizacion A",
            email="orga@example.com",
            password_hash="test-hash-a",
            role="organizacion",
            is_active=True,
        )
        org_b = User(
            full_name="Organizacion B",
            email="orgb@example.com",
            password_hash="test-hash-b",
            role="organizacion",
            is_active=True,
        )
        admin = User(
            full_name="Administrador",
            email="admin@example.com",
            password_hash="test-hash-admin",
            role="admin",
            is_active=True,
        )
        db.add_all([org_a, org_b, admin])
        db.flush()

        campaign_a = Campaign(
            title="Campaña A",
            description="Descripción A",
            organization_name="Organización A",
            status="publicada",
            created_by_user_id=org_a.id,
        )
        campaign_b = Campaign(
            title="Campaña B",
            description="Descripción B",
            organization_name="Organización B",
            status="publicada",
            created_by_user_id=org_b.id,
        )
        campaign_draft = Campaign(
            title="Campaña borrador",
            description="Todavía no publicada",
            organization_name="Organización B",
            status="borrador",
            created_by_user_id=org_b.id,
        )
        db.add_all([campaign_a, campaign_b, campaign_draft])
        db.flush()

        activity_a = Activity(
            campaign_id=campaign_a.id,
            title="Actividad A",
            description="Descripción de prueba",
            starts_at=now + timedelta(days=10),
            ends_at=now + timedelta(days=10, hours=2),
            location="Monterrey",
            capacity_people=10,
            registration_deadline=now + timedelta(days=9),
            status="publicada",
            created_by_user_id=org_a.id,
        )
        activity_b = Activity(
            campaign_id=campaign_b.id,
            title="Actividad B",
            description="Descripción de prueba",
            starts_at=now + timedelta(days=12),
            ends_at=now + timedelta(days=12, hours=2),
            location="Monterrey",
            capacity_people=10,
            registration_deadline=now + timedelta(days=11),
            status="publicada",
            created_by_user_id=org_b.id,
        )
        activity_in_draft = Activity(
            campaign_id=campaign_draft.id,
            title="Actividad en campaña borrador",
            description="Descripción de prueba",
            starts_at=now + timedelta(days=14),
            ends_at=now + timedelta(days=14, hours=2),
            location="Monterrey",
            capacity_people=10,
            registration_deadline=now + timedelta(days=13),
            status="borrador",
            created_by_user_id=org_b.id,
        )
        db.add_all([activity_a, activity_b, activity_in_draft])
        db.commit()

        ids = {
            "org_a": org_a.id,
            "org_b": org_b.id,
            "admin": admin.id,
            "campaign_a": campaign_a.id,
            "campaign_b": campaign_b.id,
            "campaign_draft": campaign_draft.id,
            "activity_a": activity_a.id,
            "activity_b": activity_b.id,
            "activity_in_draft": activity_in_draft.id,
        }

    identity = {"id": ids["org_a"], "role": "organizacion"}

    def override_user():
        return SimpleNamespace(
            id=identity["id"],
            role=identity["role"],
            is_active=True,
        )

    def override_db():
        with TestSession() as db:
            yield db

    app.dependency_overrides[get_current_user] = override_user
    app.dependency_overrides[get_db] = override_db

    try:
        with TestClient(app) as client:
            yield client, identity, ids, TestSession
    finally:
        app.dependency_overrides.clear()
        Base.metadata.drop_all(bind=engine)
        engine.dispose()


def test_organization_cannot_update_foreign_campaign(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.patch(
        f"/campaigns/{ids['campaign_b']}",
        json={"title": "Título alterado"},
    )

    assert response.status_code == 403


def test_organization_cannot_change_campaign_status(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    response = client.patch(
        f"/campaigns/{ids['campaign_a']}",
        json={"status": "cancelada"},
    )

    assert response.status_code == 403

    with TestSession() as db:
        campaign = db.get(Campaign, ids["campaign_a"])
        assert campaign.status == "publicada"


def test_admin_cannot_apply_invalid_campaign_transition(security_sandbox):
    client, identity, ids, TestSession = security_sandbox
    identity.update(id=ids["admin"], role="admin")

    response = client.patch(
        f"/campaigns/{ids['campaign_a']}",
        json={"status": "borrador"},
    )

    assert response.status_code == 409

    with TestSession() as db:
        campaign = db.get(Campaign, ids["campaign_a"])
        assert campaign.status == "publicada"


def test_draft_campaign_is_not_public(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.get(f"/campaigns/{ids['campaign_draft']}")

    assert response.status_code == 404


def test_organization_cannot_update_foreign_activity(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.patch(
        f"/activities/{ids['activity_b']}",
        json={"title": "Título alterado"},
    )

    assert response.status_code == 403


def test_organization_cannot_change_activity_status(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={"status": "cancelada"},
    )

    assert response.status_code == 403

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_a"])
        assert activity.status == "publicada"


def test_admin_cannot_publish_activity_in_draft_campaign(security_sandbox):
    client, identity, ids, TestSession = security_sandbox
    identity.update(id=ids["admin"], role="admin")

    response = client.patch(
        f"/activities/{ids['activity_in_draft']}",
        json={"status": "publicada"},
    )

    assert response.status_code == 409

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_in_draft"])
        assert activity.status == "borrador"


def test_activity_can_be_updated_without_ends_at(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={"title": "Título actualizado"},
    )

    assert response.status_code == 200, response.text
    assert response.json()["title"] == "Título actualizado"


def test_activity_in_draft_campaign_is_not_public(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.get(
        f"/activities/{ids['activity_in_draft']}"
    )

    assert response.status_code == 404


def test_patch_rejects_end_before_new_start(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_a"])
        invalid_start = (
            activity.ends_at + timedelta(hours=1)
        ).isoformat()

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={"starts_at": invalid_start},
    )

    assert response.status_code == 422


def test_patch_rejects_deadline_after_start(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_a"])
        invalid_deadline = (
            activity.starts_at + timedelta(hours=1)
        ).isoformat()

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={"registration_deadline": invalid_deadline},
    )

    assert response.status_code == 422


def test_patch_rejects_end_equal_to_start(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_a"])
        same_time = activity.starts_at.isoformat()

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={"ends_at": same_time},
    )

    assert response.status_code == 422



def test_patch_mixed_timezone_dates_returns_validation_error(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_a"])
        aware_start = activity.starts_at.replace(
            tzinfo=timezone.utc
        ).isoformat()

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={"starts_at": aware_start},
    )

    assert response.status_code == 422, response.text
