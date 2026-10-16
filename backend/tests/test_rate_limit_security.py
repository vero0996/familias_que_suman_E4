
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from main import app


def make_test_engine():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(bind=engine)
    return engine


def test_login_rate_limit_returns_429():
    engine = make_test_engine()

    def override_db():
        with Session(engine) as session:
            yield session

    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides[get_db] = override_db

    try:
        with TestClient(
            app, client=("192.0.2.10", 50000)
        ) as client:
            statuses = [
                client.post(
                    "/auth/login",
                    json={
                        "email": f"rate-limit-{i}@example.com",
                        "password": "Password-seguro-123!",
                    },
                ).status_code
                for i in range(6)
            ]

        assert statuses[:5] == [401] * 5
        assert statuses[5] == 429
    finally:
        app.dependency_overrides.clear()
        app.dependency_overrides.update(previous_overrides)
        Base.metadata.drop_all(bind=engine)
        engine.dispose()


def test_register_rate_limit_returns_429():
    engine = make_test_engine()

    def override_db():
        with Session(engine) as session:
            yield session

    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides[get_db] = override_db

    try:
        with TestClient(
            app, client=("192.0.2.11", 50001)
        ) as client:
            statuses = [
                client.post(
                    "/auth/register",
                    json={
                        "full_name": f"Usuario Prueba {i}",
                        "email": f"registro-rate-limit-{i}@example.com",
                        "phone": "8112345678",
                        "password": "Password-seguro-123!",
                        "family_name": f"Familia Prueba {i}",
                    },
                ).status_code
                for i in range(4)
            ]

        assert statuses[:3] == [201] * 3
        assert statuses[3] == 429
    finally:
        app.dependency_overrides.clear()
        app.dependency_overrides.update(previous_overrides)
        Base.metadata.drop_all(bind=engine)
        engine.dispose()
