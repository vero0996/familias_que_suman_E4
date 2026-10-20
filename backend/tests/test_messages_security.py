from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Message, User


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
):
    with Session(engine) as db:
        message = Message(
            sender_user_id=sender_user_id,
            receiver_user_id=receiver_user_id,
            text=text,
        )

        db.add(message)
        db.commit()
        db.refresh(message)

        return message.id


def test_messages_require_authentication(message_sandbox):
    client, engine = message_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

    response = client.get("/api/mensajes/2")

    assert response.status_code == 401


def test_user_can_send_message(message_sandbox):
    client, engine = message_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

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

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

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

    create_user(
        engine,
        1,
        "uno@test.com",
    )

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

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

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

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

    create_message(
        engine,
        1,
        2,
        "Hola",
    )

    create_message(
        engine,
        2,
        1,
        "Hola también",
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/2")

    assert response.status_code == 200
    assert len(response.json()) == 2


def test_conversation_contains_both_directions(message_sandbox):
    client, engine = message_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

    create_message(
        engine,
        1,
        2,
        "Primero",
    )

    create_message(
        engine,
        2,
        1,
        "Segundo",
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/2")

    messages = response.json()

    assert response.status_code == 200
    assert messages[0]["text"] == "Primero"
    assert messages[1]["text"] == "Segundo"


def test_conversation_does_not_include_other_users(message_sandbox):
    client, engine = message_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

    create_user(
        engine,
        3,
        "tres@test.com",
    )

    create_message(
        engine,
        1,
        2,
        "Mensaje correcto",
    )

    create_message(
        engine,
        1,
        3,
        "Mensaje de otro chat",
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/2")

    messages = response.json()

    assert response.status_code == 200
    assert len(messages) == 1
    assert messages[0]["text"] == "Mensaje correcto"


def test_missing_user_conversation_returns_404(message_sandbox):
    client, engine = message_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    authenticate_as(1)

    response = client.get("/api/mensajes/999")

    assert response.status_code == 404


def test_message_is_saved_in_database(message_sandbox):
    client, engine = message_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_user(
        engine,
        2,
        "dos@test.com",
    )

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