
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
from models import (
    Activity,
    Campaign,
    Family,
    FamilyMember,
    Registration,
    User,
)


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

def test_organization_cannot_list_all_admin_campaigns(security_sandbox):
    client, _, _, _ = security_sandbox

    response = client.get("/campaigns/admin")

    assert response.status_code == 403


def test_organization_lists_only_its_managed_campaigns(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.get("/campaigns/mine")

    assert response.status_code == 200, response.text
    assert {campaign["id"] for campaign in response.json()} == {
        ids["campaign_a"],
    }


def test_organization_can_create_campaign_with_photo(security_sandbox):
    client, _, _, _ = security_sandbox

    response = client.post(
        "/campaigns",
        json={
            "title": "Campaña con foto",
            "description": "Descripción",
            "organization_name": "Organización A",
            "photo_url": "/uploads/campaign.jpg",
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["photo_url"] == "/uploads/campaign.jpg"


def test_admin_can_list_campaigns_in_all_statuses(security_sandbox):
    client, identity, ids, _ = security_sandbox
    identity.update(id=ids["admin"], role="admin")

    response = client.get("/campaigns/admin")

    assert response.status_code == 200, response.text

    campaigns = response.json()
    campaign_ids = {campaign["id"] for campaign in campaigns}

    assert campaign_ids == {
        ids["campaign_a"],
        ids["campaign_b"],
        ids["campaign_draft"],
    }

    statuses = {campaign["status"] for campaign in campaigns}
    assert "publicada" in statuses
    assert "borrador" in statuses


def test_organization_lists_only_activities_it_can_manage(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.get("/activities/admin")

    assert response.status_code == 200, response.text
    assert {activity["id"] for activity in response.json()} == {
        ids["activity_a"],
    }


def test_admin_lists_activities_in_all_statuses(security_sandbox):
    client, identity, ids, _ = security_sandbox
    identity.update(id=ids["admin"], role="admin")

    response = client.get("/activities/admin")

    assert response.status_code == 200, response.text
    assert {activity["id"] for activity in response.json()} == {
        ids["activity_a"],
        ids["activity_b"],
        ids["activity_in_draft"],
    }


def test_organization_can_list_activity_registrations(security_sandbox):
    client, _, ids, TestSession = security_sandbox
    with TestSession() as db:
        family_user = User(
            full_name="Contacto",
            email="family@example.com",
            password_hash="test-hash-family",
            role="familia",
            is_active=True,
        )
        db.add(family_user)
        db.flush()
        family = Family(name="Familia de prueba", owner_user_id=family_user.id)
        db.add(family)
        db.flush()
        participant = FamilyMember(
            family_id=family.id,
            full_name="Integrante de prueba",
            age=12,
            relationship_type="Hijo",
            is_active=True,
        )
        db.add(participant)
        registration = Registration(
            activity_id=ids["activity_a"],
            family_id=family.id,
            status="confirmada",
            participants=[participant],
        )
        db.add(registration)
        db.commit()
        registration_id = registration.id

    response = client.get(
        f"/activities/admin/{ids['activity_a']}/registrations"
    )

    assert response.status_code == 200, response.text
    assert response.json() == [
        {
            "id": registration_id,
            "activity_id": ids["activity_a"],
            "family_id": family.id,
            "family_name": "Familia de prueba",
            "contact_name": "Contacto",
            "email": "family@example.com",
            "phone": None,
            "status": "confirmada",
            "participant_names": ["Integrante de prueba"],
            "people_count": 2,
            "created_at": response.json()[0]["created_at"],
        }
    ]


def test_organization_can_cancel_registration_and_promote_waitlist(
    security_sandbox,
):
    client, _, ids, TestSession = security_sandbox
    with TestSession() as db:
        registrations = []
        for index, status in enumerate(("confirmada", "lista_espera"), start=1):
            user = User(
                full_name=f"Contacto {index}",
                email=f"family{index}@example.com",
                password_hash="test-hash-family",
                role="familia",
                is_active=True,
            )
            db.add(user)
            db.flush()
            family = Family(
                name=f"Familia {index}",
                owner_user_id=user.id,
            )
            db.add(family)
            db.flush()
            registration = Registration(
                activity_id=ids["activity_a"],
                family_id=family.id,
                status=status,
            )
            db.add(registration)
            registrations.append(registration)
        db.commit()
        confirmed_id, waiting_id = [registration.id for registration in registrations]

    response = client.patch(
        f"/activities/admin/{ids['activity_a']}/registrations/{confirmed_id}/cancel"
    )

    assert response.status_code == 200, response.text
    statuses = {row["id"]: row["status"] for row in response.json()}
    assert statuses == {
        confirmed_id: "cancelada",
        waiting_id: "confirmada",
    }


def test_organization_cannot_manage_foreign_activity_registrations(
    security_sandbox,
):
    client, _, ids, _ = security_sandbox

    response = client.get(
        f"/activities/admin/{ids['activity_b']}/registrations"
    )

    assert response.status_code == 403


def test_organization_can_cancel_its_activity(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    response = client.delete(f"/activities/{ids['activity_a']}")

    assert response.status_code == 204
    with TestSession() as db:
        assert db.get(Activity, ids["activity_a"]).status == "cancelada"


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
        json={
            "title": "Título actualizado",
            "photo_url": "/uploads/activity.jpg",
        },
    )

    assert response.status_code == 200, response.text
    assert response.json()["title"] == "Título actualizado"
    assert response.json()["photo_url"] == "/uploads/activity.jpg"


def test_organization_can_create_and_edit_standalone_activity(security_sandbox):
    client, identity, ids, TestSession = security_sandbox
    starts_at = datetime.now(timezone.utc) + timedelta(days=20)
    response = client.post(
        "/activities",
        json={
            "campaign_id": None,
            "title": "Actividad independiente",
            "description": "No pertenece a una campaña",
            "starts_at": starts_at.isoformat(),
            "ends_at": (starts_at + timedelta(hours=2)).isoformat(),
            "location": "Monterrey",
            "capacity_people": 10,
            "registration_deadline": (
                starts_at - timedelta(days=1)
            ).isoformat(),
        },
    )

    assert response.status_code == 201, response.text
    activity_id = response.json()["id"]
    assert response.json()["campaign_id"] is None

    update_response = client.patch(
        f"/activities/{activity_id}",
        json={"title": "Actividad independiente editada"},
    )
    assert update_response.status_code == 200, update_response.text
    assert update_response.json()["title"] == "Actividad independiente editada"

    identity.update(id=ids["admin"], role="admin")
    publish_response = client.patch(
        f"/activities/{activity_id}",
        json={"status": "publicada"},
    )
    assert publish_response.status_code == 200, publish_response.text

    list_response = client.get("/activities")
    assert list_response.status_code == 200, list_response.text
    assert activity_id in {
        activity["id"] for activity in list_response.json()
    }

    detail_response = client.get(f"/activities/{activity_id}")
    assert detail_response.status_code == 200, detail_response.text
    assert detail_response.json()["campaign_id"] is None

    with TestSession() as db:
        activity = db.get(Activity, activity_id)
        assert activity.campaign_id is None


def test_activity_create_requires_registration_deadline(security_sandbox):
    client, _, _, _ = security_sandbox
    starts_at = datetime.now(timezone.utc) + timedelta(days=20)

    response = client.post(
        "/activities",
        json={
            "campaign_id": None,
            "title": "Actividad independiente",
            "description": "Falta la fecha límite",
            "starts_at": starts_at.isoformat(),
            "ends_at": (starts_at + timedelta(hours=2)).isoformat(),
            "location": "Monterrey",
            "capacity_people": 10,
        },
    )

    assert response.status_code == 422, response.text


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

def test_campaign_patch_cannot_change_owner_or_id(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    response = client.patch(
        f"/campaigns/{ids['campaign_a']}",
        json={
            "title": "Título actualizado",
            "id": ids["campaign_b"],
            "created_by_user_id": ids["org_b"],
        },
    )

    assert response.status_code == 200
    assert response.json()["title"] == "Título actualizado"
    assert response.json()["id"] == ids["campaign_a"]
    assert response.json()["created_by_user_id"] == ids["org_a"]

    with TestSession() as db:
        campaign = db.get(Campaign, ids["campaign_a"])
        assert campaign.created_by_user_id == ids["org_a"]


def test_activity_patch_cannot_change_owner_campaign_or_id(security_sandbox):
    client, _, ids, TestSession = security_sandbox

    response = client.patch(
        f"/activities/{ids['activity_a']}",
        json={
            "title": "Actividad actualizada",
            "id": ids["activity_b"],
            "campaign_id": ids["campaign_b"],
            "created_by_user_id": ids["org_b"],
        },
    )

    assert response.status_code == 200
    assert response.json()["title"] == "Actividad actualizada"
    assert response.json()["id"] == ids["activity_a"]
    assert response.json()["campaign_id"] == ids["campaign_a"]
    assert response.json()["created_by_user_id"] == ids["org_a"]

    with TestSession() as db:
        activity = db.get(Activity, ids["activity_a"])
        assert activity.campaign_id == ids["campaign_a"]
        assert activity.created_by_user_id == ids["org_a"]

def test_organization_cannot_create_activity_in_foreign_campaign(security_sandbox):
    client, _, ids, _ = security_sandbox

    response = client.post(
        "/activities",
        json={
            "campaign_id": ids["campaign_b"],
            "title": "Actividad ajena",
            "description": "Prueba de seguridad",
            "starts_at": (datetime.now(timezone.utc) + timedelta(days=20)).isoformat(),
            "ends_at": (datetime.now(timezone.utc) + timedelta(days=20, hours=2)).isoformat(),
            "location": "Monterrey",
            "capacity_people": 10,
            "registration_deadline": (datetime.now(timezone.utc) + timedelta(days=19)).isoformat(),
        },
    )

    assert response.status_code == 403