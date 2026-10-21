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
from models import Activity, Event, Message, User, Workshop


@pytest.fixture
def message_sandbox():
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


def create_user(engine, user_id: int, email: str):
    with Session(engine) as db:
        user = User(
            id=user_id,
            full_name=f"Usuario {user_id}",
            email=email,
            phone=None,
            password_hash="hash",
            role="familia",
            is_active=True,
        )

        db.add(user)
        db.commit()


def create_activity(engine, activity_id: int):
    now = datetime.now(timezone.utc)

    with Session(engine) as db:
        activity = Activity(
            id=activity_id,
            campaign_id=None,
            title="Actividad de prueba",
            description="Actividad para pruebas",
            starts_at=now + timedelta(days=1),
            ends_at=now + timedelta(days=1, hours=2),
            location="Monterrey",
            capacity_people=20,
            registration_deadline=now,
            status="publicada",
            created_by_user_id=None,
        )

        db.add(activity)
        db.commit()


def create_event(engine, event_id: int):
    with Session(engine) as db:
        event = Event(
            id=event_id,
            title="Evento de prueba",
            description="Evento para pruebas",
            location="Monterrey",
            starts_at=datetime.now(timezone.utc) + timedelta(days=1),
        )

        db.add(event)
        db.commit()


def create_workshop(engine, workshop_id: int):
    with Session(engine) as db:
        workshop = Workshop(
            id=workshop_id,
            title="Taller de prueba",
            description="Taller para pruebas",
            location="Monterrey",
            starts_at=datetime.now(timezone.utc) + timedelta(days=1),
        )

        db.add(workshop)
        db.commit()


def authenticate_as(user_id: int):
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=user_id,
        role="familia",
        is_active=True,
    )


def create_message(
    engine,
    sender_user_id: int,
    receiver_user_id: int,
    text: str,
    activity_id: int | None = None,
    event_id: int | None = None,
    workshop_id: int | None = None,
):
    with Session(engine) as db:
        message = Message(
            sender_user_id=sender_user_id,
            receiver_user_id=receiver_user_id,
            text=text,
            activity_id=activity_id,
            event_id=event_id,
            workshop_id=workshop_id,
        )

        db.add(message)
        db.commit()
        db.refresh(message)

        return message.id


def test_messages_require_authentication(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")

    response = client.get("/api/mensajes/2")

    assert response.status_code == 401


def test_user_can_send_message(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "text": "Hola",
        },
    )

    assert response.status_code == 201
    assert response.json()["sender_user_id"] == 1
    assert response.json()["receiver_user_id"] == 2
    assert response.json()["text"] == "Hola"


def test_sender_is_current_user(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "text": "Mensaje de prueba",
        },
    )

    assert response.status_code == 201
    assert response.json()["sender_user_id"] == 1


def test_missing_receiver_returns_404(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 999,
            "text": "Hola",
        },
    )

    assert response.status_code == 404


def test_empty_message_is_rejected(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "text": "",
        },
    )

    assert response.status_code == 422


def test_user_can_get_conversation(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")

    create_message(engine, 1, 2, "Hola")
    create_message(engine, 2, 1, "Hola también")

    authenticate_as(1)

    response = client.get("/api/mensajes/2")

    assert response.status_code == 200
    assert len(response.json()) == 2


def test_conversation_contains_both_directions(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")

    create_message(engine, 1, 2, "Primero")
    create_message(engine, 2, 1, "Segundo")

    authenticate_as(1)

    response = client.get("/api/mensajes/2")
    messages = response.json()

    assert response.status_code == 200
    assert messages[0]["text"] == "Primero"
    assert messages[1]["text"] == "Segundo"


def test_conversation_does_not_include_other_users(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_user(engine, 3, "tres@test.com")

    create_message(engine, 1, 2, "Mensaje correcto")
    create_message(engine, 1, 3, "Mensaje de otro chat")

    authenticate_as(1)

    response = client.get("/api/mensajes/2")
    messages = response.json()

    assert response.status_code == 200
    assert len(messages) == 1
    assert messages[0]["text"] == "Mensaje correcto"


def test_missing_user_conversation_returns_404(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    authenticate_as(1)

    response = client.get("/api/mensajes/999")

    assert response.status_code == 404


def test_message_is_saved_in_database(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "text": "Mensaje guardado",
        },
    )

    assert response.status_code == 201

    with Session(engine) as db:
        message = db.query(Message).first()

        assert message is not None
        assert message.sender_user_id == 1
        assert message.receiver_user_id == 2
        assert message.text == "Mensaje guardado"


def test_user_can_send_message_with_activity_context(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_activity(engine, 1)
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "activity_id": 1,
            "text": "Mensaje de actividad",
        },
    )

    assert response.status_code == 201
    assert response.json()["activity_id"] == 1
    assert response.json()["event_id"] is None
    assert response.json()["workshop_id"] is None
    assert response.json()["text"] == "Mensaje de actividad"


def test_user_can_get_activity_conversation(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_activity(engine, 1)

    create_message(
        engine,
        sender_user_id=1,
        receiver_user_id=2,
        text="Mensaje actividad",
        activity_id=1,
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/actividad/1/2")

    assert response.status_code == 200
    assert len(response.json()) == 1
    assert response.json()[0]["activity_id"] == 1
    assert response.json()[0]["text"] == "Mensaje actividad"


def test_user_can_send_message_with_event_context(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_event(engine, 1)
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "event_id": 1,
            "text": "Mensaje de evento",
        },
    )

    assert response.status_code == 201
    assert response.json()["activity_id"] is None
    assert response.json()["event_id"] == 1
    assert response.json()["workshop_id"] is None
    assert response.json()["text"] == "Mensaje de evento"


def test_user_can_get_event_conversation(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_event(engine, 1)

    create_message(
        engine,
        sender_user_id=1,
        receiver_user_id=2,
        text="Mensaje evento",
        event_id=1,
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/evento/1/2")

    assert response.status_code == 200
    assert len(response.json()) == 1
    assert response.json()[0]["event_id"] == 1
    assert response.json()[0]["text"] == "Mensaje evento"


def test_user_can_send_message_with_workshop_context(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_workshop(engine, 1)
    authenticate_as(1)

    response = client.post(
        "/api/mensajes/",
        json={
            "receiver_user_id": 2,
            "workshop_id": 1,
            "text": "Mensaje de taller",
        },
    )

    assert response.status_code == 201
    assert response.json()["activity_id"] is None
    assert response.json()["event_id"] is None
    assert response.json()["workshop_id"] == 1
    assert response.json()["text"] == "Mensaje de taller"


def test_user_can_get_workshop_conversation(message_sandbox):
    client, engine = message_sandbox

    create_user(engine, 1, "uno@test.com")
    create_user(engine, 2, "dos@test.com")
    create_workshop(engine, 1)

    create_message(
        engine,
        sender_user_id=1,
        receiver_user_id=2,
        text="Mensaje taller",
        workshop_id=1,
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/taller/1/2")

    assert response.status_code == 200
    assert len(response.json()) == 1
    assert response.json()[0]["workshop_id"] == 1
    assert response.json()[0]["text"] == "Mensaje taller"