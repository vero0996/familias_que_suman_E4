from datetime import datetime, timedelta, timezone

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from main import app
from models import Activity, Campaign, Family, FamilyMember, Registration, User


@pytest.fixture
def activity_occupancy_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    now = datetime.now(timezone.utc)
    with Session(engine) as db:
        campaign = Campaign(
            title="Campaña de cupos",
            description="Prueba de ocupación",
            organization_name="Organización de prueba",
            status="publicada",
        )
        db.add(campaign)
        db.flush()

        occupied_activity = Activity(
            campaign_id=campaign.id,
            title="Actividad con participantes",
            description="Actividad usada para probar cupos",
            starts_at=now + timedelta(days=10),
            ends_at=now + timedelta(days=10, hours=2),
            location="Lugar de prueba",
            capacity_people=4,
            registration_deadline=now + timedelta(days=9),
            status="publicada",
        )
        empty_activity = Activity(
            campaign_id=campaign.id,
            title="Actividad vacía",
            description="Actividad sin inscripciones",
            starts_at=now + timedelta(days=12),
            ends_at=now + timedelta(days=12, hours=2),
            location="Lugar de prueba",
            capacity_people=10,
            registration_deadline=now + timedelta(days=11),
            status="publicada",
        )

        families = []
        for index in range(4):
            user = User(
                full_name=f"Familia {index}",
                email=f"familia-{index}@test.invalid",
                password_hash="test-only-hash",
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
            families.append(family)

        confirmed_member_one = FamilyMember(
            family_id=families[0].id,
            full_name="Acompañante confirmado 1",
            relationship_type="Hijo",
            is_active=True,
        )
        confirmed_member_two = FamilyMember(
            family_id=families[0].id,
            full_name="Acompañante confirmado 2",
            relationship_type="Hijo",
            is_active=True,
        )
        waiting_member = FamilyMember(
            family_id=families[2].id,
            full_name="Acompañante en espera",
            relationship_type="Hijo",
            is_active=True,
        )
        cancelled_member = FamilyMember(
            family_id=families[3].id,
            full_name="Acompañante cancelado",
            relationship_type="Hijo",
            is_active=True,
        )
        db.add_all(
            [
                occupied_activity,
                empty_activity,
                confirmed_member_one,
                confirmed_member_two,
                waiting_member,
                cancelled_member,
            ]
        )
        db.flush()

        confirmed_with_members = Registration(
            activity_id=occupied_activity.id,
            family_id=families[0].id,
            status="confirmada",
        )
        confirmed_with_members.participants = [
            confirmed_member_one,
            confirmed_member_two,
        ]
        confirmed_without_members = Registration(
            activity_id=occupied_activity.id,
            family_id=families[1].id,
            status="confirmada",
        )
        waiting_registration = Registration(
            activity_id=occupied_activity.id,
            family_id=families[2].id,
            status="lista_espera",
        )
        waiting_registration.participants = [waiting_member]

        canceled_registration = Registration(
            activity_id=occupied_activity.id,
            family_id=families[3].id,
            status="cancelada",
        )
        canceled_registration.participants = [cancelled_member]

        db.add_all(
            [
                confirmed_with_members,
                confirmed_without_members,
                waiting_registration,
                canceled_registration,
            ]
        )
        db.commit()
        occupied_activity_id = occupied_activity.id
        empty_activity_id = empty_activity.id

    def override_db():
        with Session(engine) as db:
            yield db

    app.dependency_overrides[get_db] = override_db

    try:
        with TestClient(app) as client:
            yield client, occupied_activity_id, empty_activity_id
    finally:
        app.dependency_overrides.clear()
        Base.metadata.drop_all(engine)
        engine.dispose()


def test_activity_endpoints_report_confirmed_people_only(
    activity_occupancy_sandbox,
):
    client, occupied_activity_id, empty_activity_id = activity_occupancy_sandbox

    list_response = client.get("/activities")

    assert list_response.status_code == 200
    activities = {
        activity["id"]: activity
        for activity in list_response.json()
    }
    assert activities[occupied_activity_id]["capacity_people"] == 4
    assert activities[occupied_activity_id]["confirmed_people"] == 4
    assert activities[empty_activity_id]["capacity_people"] == 10
    assert activities[empty_activity_id]["confirmed_people"] == 0

    detail_response = client.get(f"/activities/{occupied_activity_id}")

    assert detail_response.status_code == 200
    assert detail_response.json()["confirmed_people"] == 4
