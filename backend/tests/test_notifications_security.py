from datetime import datetime, timedelta, timezone
from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine, select
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

import notifications
from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import (
    Activity,
    Campaign,
    Family,
    Notification,
    Registration,
    User,
)


@pytest.fixture
def notification_sandbox():
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


def create_user(
    engine,
    user_id: int,
    email: str,
):
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


def create_notification(
    engine,
    user_id: int,
    title: str,
    notification_type: str,
):
    with Session(engine) as db:
        notification = Notification(
            user_id=user_id,
            title=title,
            message="Mensaje de prueba",
            notification_type=notification_type,
        )

        db.add(notification)
        db.commit()
        db.refresh(notification)

        return notification.id


def create_confirmed_registration(
    engine,
    user_id: int,
    starts_at: datetime,
):
    with Session(engine) as db:
        family = Family(
            name="Familia Prueba",
            owner_user_id=user_id,
        )

        db.add(family)
        db.flush()

        campaign = Campaign(
            title="Campaña de prueba",
            description="Prueba",
            organization_name="Organización Prueba",
            status="publicada",
        )

        db.add(campaign)
        db.flush()

        activity = Activity(
            campaign_id=campaign.id,
            title="Actividad de prueba",
            description="Actividad para probar recordatorios",
            starts_at=starts_at,
            ends_at=starts_at + timedelta(hours=2),
            location="Monterrey",
            capacity_people=20,
            registration_deadline=starts_at - timedelta(hours=1),
            status="publicada",
        )

        db.add(activity)
        db.flush()

        registration = Registration(
            activity_id=activity.id,
            family_id=family.id,
            status="confirmada",
        )

        db.add(registration)
        db.commit()

        return activity.id


def test_notifications_require_authentication(notification_sandbox):
    client, engine = notification_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    response = client.get("/api/notificaciones")

    assert response.status_code == 401


def test_user_can_get_own_notifications(notification_sandbox):
    client, engine = notification_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    create_notification(
        engine,
        1,
        "Notificación propia",
        "test_notification",
    )

    authenticate_as(1)

    response = client.get("/api/notificaciones")

    assert response.status_code == 200
    assert len(response.json()) == 1
    assert response.json()[0]["title"] == "Notificación propia"


def test_user_cannot_get_other_users_notifications(
    notification_sandbox,
):
    client, engine = notification_sandbox

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

    create_notification(
        engine,
        2,
        "Notificación de otro usuario",
        "other_notification",
    )

    authenticate_as(1)

    response = client.get("/api/notificaciones")

    assert response.status_code == 200
    assert response.json() == []


def test_user_can_mark_notification_as_read(
    notification_sandbox,
):
    client, engine = notification_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    notification_id = create_notification(
        engine,
        1,
        "Notificación",
        "test_notification",
    )

    authenticate_as(1)

    response = client.patch(
        f"/api/notificaciones/{notification_id}/leer"
    )

    assert response.status_code == 200
    assert response.json()["is_read"] is True


def test_user_cannot_mark_other_notification_as_read(
    notification_sandbox,
):
    client, engine = notification_sandbox

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

    notification_id = create_notification(
        engine,
        2,
        "Notificación de otro usuario",
        "other_notification",
    )

    authenticate_as(1)

    response = client.patch(
        f"/api/notificaciones/{notification_id}/leer"
    )

    assert response.status_code == 403


def test_missing_notification_returns_404(
    notification_sandbox,
):
    client, engine = notification_sandbox

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    authenticate_as(1)

    response = client.patch(
        "/api/notificaciones/999/leer"
    )

    assert response.status_code == 404


def test_reminder_24_hours_is_created(
    notification_sandbox,
    monkeypatch,
):
    client, engine = notification_sandbox

    now = datetime(
        2026,
        10,
        20,
        12,
        0,
        tzinfo=timezone.utc,
    )

    monkeypatch.setattr(
        notifications,
        "utc_now",
        lambda: now,
    )

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    activity_id = create_confirmed_registration(
        engine,
        1,
        now + timedelta(hours=22),
    )

    authenticate_as(1)

    response = client.get("/api/notificaciones")

    assert response.status_code == 200

    data = response.json()

    assert len(data) == 1
    assert data[0]["title"] == "Recordatorio de actividad"
    assert data[0]["notification_type"] == f"activity_24h_{activity_id}"
    assert data[0]["is_read"] is False


def test_same_day_reminder_is_created(
    notification_sandbox,
    monkeypatch,
):
    client, engine = notification_sandbox

    now = datetime(
        2026,
        10,
        20,
        10,
        0,
        tzinfo=timezone.utc,
    )

    monkeypatch.setattr(
        notifications,
        "utc_now",
        lambda: now,
    )

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    activity_id = create_confirmed_registration(
        engine,
        1,
        now + timedelta(hours=5),
    )

    authenticate_as(1)

    response = client.get("/api/notificaciones")

    assert response.status_code == 200

    data = response.json()

    assert len(data) == 1
    assert data[0]["title"] == "Recordatorio de actividad"
    assert data[0]["notification_type"] == f"activity_today_{activity_id}"
    assert data[0]["is_read"] is False


def test_reminder_is_not_created_twice(
    notification_sandbox,
    monkeypatch,
):
    client, engine = notification_sandbox

    now = datetime(
        2026,
        10,
        20,
        12,
        0,
        tzinfo=timezone.utc,
    )

    monkeypatch.setattr(
        notifications,
        "utc_now",
        lambda: now,
    )

    create_user(
        engine,
        1,
        "uno@test.com",
    )

    activity_id = create_confirmed_registration(
        engine,
        1,
        now + timedelta(hours=22),
    )

    authenticate_as(1)

    first_response = client.get("/api/notificaciones")
    second_response = client.get("/api/notificaciones")

    assert first_response.status_code == 200
    assert second_response.status_code == 200

    with Session(engine) as db:
        notifications_found = db.scalars(
            select(Notification).where(
                Notification.user_id == 1,
                Notification.notification_type
                == f"activity_24h_{activity_id}",
            )
        ).all()

        assert len(notifications_found) == 1