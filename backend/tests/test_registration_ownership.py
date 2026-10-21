
from datetime import datetime, timedelta, timezone
from types import SimpleNamespace
from sqlalchemy import select
from models import RegistrationEvent
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


def test_family_can_register_for_standalone_activity(registration_sandbox):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.campaign_id = None
        db.commit()

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

def test_family_registration_goes_to_waitlist_when_capacity_is_exceeded(
    registration_sandbox,
):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.capacity_people = 1
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["status"] == "lista_espera"

def test_cancel_confirmed_registration_promotes_waitlisted_family(
    registration_sandbox,
):
    client, engine, own_id, foreign_id, activity_id, *_ = registration_sandbox

    with Session(engine) as db:
        confirmed = db.get(Registration, own_id)
        waiting = db.get(Registration, foreign_id)

        # Mover ambas inscripciones a la misma actividad.
        confirmed.activity_id = activity_id
        waiting.activity_id = activity_id
        waiting.status = "lista_espera"

        activity = db.get(Activity, activity_id)
        activity.capacity_people = 1
        db.commit()

        waiting_id = waiting.id

    response = client.patch(
        f"/registrations/{own_id}/cancel",
        json={"reason": "Prueba de liberación de cupo"},
    )

    assert response.status_code == 200, response.text

    with Session(engine) as db:
        assert db.get(Registration, own_id).status == "cancelada"
        assert db.get(Registration, waiting_id).status == "confirmada"

def test_cancel_does_not_promote_waitlist_after_activity_starts(
    registration_sandbox,
):
    client, engine, own_id, foreign_id, activity_id, *_ = registration_sandbox

    with Session(engine) as db:
        confirmed = db.get(Registration, own_id)
        waiting = db.get(Registration, foreign_id)

        confirmed.activity_id = activity_id
        waiting.activity_id = activity_id
        waiting.status = "lista_espera"

        activity = db.get(Activity, activity_id)
        activity.starts_at = datetime.now(timezone.utc) - timedelta(minutes=5)
        db.commit()

        waiting_id = waiting.id

    response = client.patch(
        f"/registrations/{own_id}/cancel",
        json={"reason": "Prueba de actividad iniciada"},
    )

    assert response.status_code == 200, response.text

    with Session(engine) as db:
        assert db.get(Registration, waiting_id).status == "lista_espera"

def test_cannot_cancel_registration_twice(registration_sandbox):
    client, _, own_id, *_ = registration_sandbox

    first = client.patch(
        f"/registrations/{own_id}/cancel",
        json={"reason": "Primera cancelación"},
    )
    assert first.status_code == 200, first.text

    second = client.patch(
        f"/registrations/{own_id}/cancel",
        json={"reason": "Segunda cancelación"},
    )
    assert second.status_code == 409, second.text

def test_family_cannot_register_after_activity_starts(registration_sandbox):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.starts_at = datetime.now(timezone.utc) - timedelta(minutes=5)
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_cannot_register_after_deadline(registration_sandbox):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.registration_deadline = (
            datetime.now(timezone.utc) - timedelta(minutes=5)
        )
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_cannot_register_in_draft_campaign(registration_sandbox):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        campaign = db.get(Campaign, activity.campaign_id)
        campaign.status = "borrador"
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_cannot_register_in_draft_activity(registration_sandbox):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.status = "borrador"
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_can_register_again_after_cancellation(registration_sandbox):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(
        f"/registrations/{own_id}/cancel"
    )
    assert cancel_response.status_code == 200, cancel_response.text

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["status"] in ("confirmada", "lista_espera")
    assert response.json()["id"] == own_id

def test_reactivated_registration_clears_canceled_at(registration_sandbox):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(
        f"/registrations/{own_id}/cancel"
    )
    assert cancel_response.status_code == 200, cancel_response.text

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        assert registration.canceled_at is not None

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["id"] == own_id
    assert response.json()["canceled_at"] is None

def test_reactivation_creates_history_event(registration_sandbox):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )
    assert response.status_code == 201, response.text

    with Session(engine) as db:
        events = db.scalars(
            select(RegistrationEvent).where(
                RegistrationEvent.registration_id == own_id
            )
        ).all()

        assert any(
            event.event_type == "inscripcion_reactivada"
            for event in events
        )

def test_reactivation_updates_participants(registration_sandbox):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id
        family_id = registration.family_id

        # Crear otro integrante activo de la misma familia.
        new_member = FamilyMember(
            family_id=family_id,
            full_name="Integrante nuevo",
            age=8,
            relationship_type="Hijo",
            is_active=True,
)
        db.add(new_member)
        db.commit()
        db.refresh(new_member)
        new_member_id = new_member.id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [new_member_id],
        },
    )

    assert response.status_code == 201, response.text

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        participant_ids = {member.id for member in registration.participants}

    assert participant_ids == {new_member_id}
    assert own_member_id not in participant_ids

def test_family_cannot_reactivate_registration_after_activity_starts(
    registration_sandbox,
):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.starts_at = datetime.now(timezone.utc) - timedelta(minutes=5)
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_cannot_reactivate_registration_after_deadline(
    registration_sandbox,
):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.registration_deadline = (
            datetime.now(timezone.utc) - timedelta(minutes=5)
        )
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_cannot_reactivate_registration_for_unpublished_activity(
    registration_sandbox,
):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.status = "borrador"
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_family_cannot_reactivate_registration_for_unpublished_campaign(
    registration_sandbox,
):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        campaign = db.get(Campaign, activity.campaign_id)
        campaign.status = "borrador"
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 409, response.text

def test_reactivation_event_records_previous_and_new_status(
    registration_sandbox,
):
    client, engine, own_id, _, _, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        registration = db.get(Registration, own_id)
        activity_id = registration.activity_id

    cancel_response = client.patch(f"/registrations/{own_id}/cancel")
    assert cancel_response.status_code == 200, cancel_response.text

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )
    assert response.status_code == 201, response.text

    with Session(engine) as db:
        event = db.scalar(
            select(RegistrationEvent).where(
                RegistrationEvent.registration_id == own_id,
                RegistrationEvent.event_type == "inscripcion_reactivada",
            )
        )

        assert event is not None
        assert event.from_status == "cancelada"
        assert event.to_status == response.json()["status"]

def test_registration_is_confirmed_when_family_exactly_fits(
    registration_sandbox,
):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        # Ya hay un titular inscrito. Quedan dos lugares.
        activity.capacity_people = 3
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["status"] == "confirmada"

    with Session(engine) as db:
        registration = db.get(Registration, response.json()["id"])
        assert registration.status == "confirmada"
        assert {member.id for member in registration.participants} == {
            own_member_id
        }

        confirmed = db.scalars(
            select(Registration).where(
                Registration.activity_id == activity_id,
                Registration.status == "confirmada",
            )
        ).all()

        occupied = sum(1 + len(item.participants) for item in confirmed)
        assert occupied == 3
        assert occupied == db.get(Activity, activity_id).capacity_people


def test_registration_waits_when_only_one_of_two_required_places_is_free(
    registration_sandbox,
):
    client, engine, _, _, activity_id, own_member_id, *_ = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        # Ya hay un titular inscrito. Solo queda un lugar.
        activity.capacity_people = 2
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["status"] == "lista_espera"

    with Session(engine) as db:
        registration = db.get(Registration, response.json()["id"])
        assert registration.status == "lista_espera"
        assert {member.id for member in registration.participants} == {
            own_member_id
        }

        confirmed = db.scalars(
            select(Registration).where(
                Registration.activity_id == activity_id,
                Registration.status == "confirmada",
            )
        ).all()

        occupied = sum(1 + len(item.participants) for item in confirmed)
        assert occupied == 1

@pytest.mark.parametrize("oldest_people", [2, 3])
def test_waitlist_promotes_oldest_family_that_fits(
    registration_sandbox,
    oldest_people,
):
    (
        client, engine, own_id, foreign_id, activity_id,
        own_member_id, foreign_member_id, _,
    ) = registration_sandbox

    with Session(engine) as db:
        activity = db.get(Activity, activity_id)
        activity.capacity_people = 2

        confirmed = db.get(Registration, own_id)
        confirmed.activity_id = activity_id
        confirmed.participants = [db.get(FamilyMember, own_member_id)]

        oldest = db.get(Registration, foreign_id)
        oldest.status = "lista_espera"
        oldest.created_at = datetime.now(timezone.utc) - timedelta(days=2)
        oldest.participants = [db.get(FamilyMember, foreign_member_id)]

        if oldest_people == 3:
            extra_member = FamilyMember(
                family_id=oldest.family_id,
                full_name="Otro acompañante",
                age=9,
                relationship_type="Hijo",
                is_active=True,
            )
            db.add(extra_member)
            oldest.participants.append(extra_member)

        newer_user = User(
            full_name="Familia C",
            email="familia-c@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        db.add(newer_user)
        db.flush()

        newer_family = Family(
            name="Familia C",
            owner_user_id=newer_user.id,
        )
        db.add(newer_family)
        db.flush()

        newer_member = FamilyMember(
            family_id=newer_family.id,
            full_name="Acompañante C",
            age=10,
            relationship_type="Hijo",
            is_active=True,
        )
        db.add(newer_member)
        db.flush()

        newer = Registration(
            activity_id=activity_id,
            family_id=newer_family.id,
            status="lista_espera",
            created_at=datetime.now(timezone.utc) - timedelta(days=1),
            participants=[newer_member],
        )
        db.add(newer)
        db.commit()
        newer_id = newer.id

    response = client.patch(
        f"/registrations/{own_id}/cancel",
        json={"reason": "Probar orden y capacidad de la lista de espera"},
    )

    assert response.status_code == 200, response.text

    promoted_id = foreign_id if oldest_people == 2 else newer_id
    waiting_id = newer_id if oldest_people == 2 else foreign_id

    with Session(engine) as db:
        assert db.get(Registration, own_id).status == "cancelada"
        assert db.get(Registration, promoted_id).status == "confirmada"
        assert db.get(Registration, waiting_id).status == "lista_espera"

        confirmed = db.scalars(
            select(Registration).where(
                Registration.activity_id == activity_id,
                Registration.status == "confirmada",
            )
        ).all()

        occupied = sum(1 + len(item.participants) for item in confirmed)
        assert occupied == 2
        assert occupied <= db.get(Activity, activity_id).capacity_people

        promotion = db.scalar(
            select(RegistrationEvent).where(
                RegistrationEvent.registration_id == promoted_id,
                RegistrationEvent.event_type == "lista_espera_promovida",
            )
        )
        assert promotion is not None
        assert promotion.from_status == "lista_espera"
        assert promotion.to_status == "confirmada"

        cancellation = db.scalar(
            select(RegistrationEvent).where(
                RegistrationEvent.registration_id == own_id,
                RegistrationEvent.event_type == "inscripcion_cancelada",
            )
        )
        assert cancellation is not None
        assert cancellation.from_status == "confirmada"
        assert cancellation.to_status == "cancelada"

def test_reactivated_family_returns_to_end_of_waitlist(
    registration_sandbox,
):
    (
        client, engine, own_id, foreign_id, activity_id,
        own_member_id, foreign_member_id, _,
    ) = registration_sandbox

    with Session(engine) as db:
        now = datetime.now(timezone.utc)
        activity = db.get(Activity, activity_id)
        activity.capacity_people = 2

        returning = db.get(Registration, own_id)
        returning.activity_id = activity_id
        returning.status = "cancelada"
        returning.created_at = now - timedelta(days=3)
        returning.canceled_at = now - timedelta(days=2)

        waiting = db.get(Registration, foreign_id)
        waiting.status = "lista_espera"
        waiting.created_at = now - timedelta(days=1)
        waiting.participants = [db.get(FamilyMember, foreign_member_id)]

        holder_user = User(
            full_name="Familia con lugares",
            email="familia-con-lugares@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        db.add(holder_user)
        db.flush()

        holder_family = Family(
            name="Familia con lugares",
            owner_user_id=holder_user.id,
        )
        db.add(holder_family)
        db.flush()

        holder_member = FamilyMember(
            family_id=holder_family.id,
            full_name="Acompañante",
            age=10,
            relationship_type="Hijo",
            is_active=True,
        )
        db.add(holder_member)
        db.flush()

        holder = Registration(
            activity_id=activity_id,
            family_id=holder_family.id,
            status="confirmada",
            participants=[holder_member],
        )
        db.add(holder)
        db.commit()

        holder_id = holder.id
        holder_user_id = holder_user.id

    # La familia que canceló vuelve a inscribirse cuando el cupo está lleno.
    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [own_member_id],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["id"] == own_id
    assert response.json()["status"] == "lista_espera"

    # Cambiar al usuario que tiene los lugares para que cancele su inscripción.
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=holder_user_id,
        role="familia",
        is_active=True,
    )

    response = client.patch(f"/registrations/{holder_id}/cancel")
    assert response.status_code == 200, response.text

    with Session(engine) as db:
        # Debe avanzar quien ya estaba esperando, no quien se reinscribió.
        assert db.get(Registration, foreign_id).status == "confirmada"
        assert db.get(Registration, own_id).status == "lista_espera"

@pytest.mark.parametrize(
    "capacity, expected_status",
    [
        (2, "confirmada"),
        (1, "lista_espera"),
    ],
)
def test_holder_alone_occupies_one_place(
    registration_sandbox,
    capacity,
    expected_status,
):
    client, engine, _, _, activity_id, *_ = registration_sandbox

    with Session(engine) as db:
        # Esta actividad ya tiene un titular confirmado.
        activity = db.get(Activity, activity_id)
        activity.capacity_people = capacity
        db.commit()

    response = client.post(
        "/registrations",
        json={
            "activity_id": activity_id,
            "participant_ids": [],
        },
    )

    assert response.status_code == 201, response.text
    assert response.json()["status"] == expected_status

    with Session(engine) as db:
        registration = db.get(Registration, response.json()["id"])
        assert registration.status == expected_status
        assert registration.participants == []

        confirmed = db.scalars(
            select(Registration).where(
                Registration.activity_id == activity_id,
                Registration.status == "confirmada",
            )
        ).all()

        occupied = sum(1 + len(item.participants) for item in confirmed)
        assert occupied == capacity