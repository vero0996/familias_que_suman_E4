
from datetime import datetime, timezone

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import select, create_engine
from sqlalchemy.orm import Session
from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import User

@pytest.fixture
def admin_sandbox(tmp_path):
    engine = create_engine(
        f"sqlite:///{tmp_path / 'admin_test.db'}",
        connect_args={"check_same_thread": False},
    )
    Base.metadata.create_all(engine)

    def override_db():
        with Session(engine) as session:
            print("DB DE PRUEBA:", session.get_bind().url)
            yield session

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
    db: Session,
    *,
    email: str,
    role: str = "familia",
    is_active: bool = True,
) -> User:
    user = User(
        full_name="Usuario de prueba",
        email=email,
        phone=None,
        password_hash="hash-de-prueba",
        role=role,
        is_active=is_active,
        created_at=datetime.now(timezone.utc),
    )
    db.add(user)
    db.commit()
    db.refresh(user)
    return user

def authenticate_as(role: str, user_id: int = 999):
    from types import SimpleNamespace

    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=user_id,
        full_name="Administrador de prueba",
        email="admin@example.com",
        phone=None,
        password_hash="hash-de-prueba",
        role=role,
        is_active=True,
    )

def test_promote_organization_requires_authentication(admin_sandbox):
    client, _ = admin_sandbox

    response = client.post(
        "/admin/organizations",
        json={"email": "familia@example.com"},
    )

    assert response.status_code == 401


@pytest.mark.parametrize("role", ["familia", "organizacion"])
def test_non_admin_cannot_promote_organization(admin_sandbox, role):
    client, engine = admin_sandbox

    with Session(engine) as db:
        target = create_user(
            db,
            email="familia@example.com",
        )
        target_id = target.id

    authenticate_as(role)

    response = client.post(
        "/admin/organizations",
        json={"email": "familia@example.com"},
    )

    assert response.status_code == 403

    with Session(engine) as db:
        target = db.get(User, target_id)
        assert target.role == "familia"


def test_admin_can_promote_family_to_organization(admin_sandbox):
    client, engine = admin_sandbox

    with Session(engine) as db:
        target = create_user(
            db,
            email="familia@example.com",
        )
        target_id = target.id

    authenticate_as("admin")

    response = client.post(
        "/admin/organizations",
        json={"email": "familia@example.com"},
    )

    assert response.status_code == 200, response.text
    assert response.json()["role"] == "organizacion"

    with Session(engine) as db:
        target = db.get(User, target_id)
        assert target.role == "organizacion"


def test_admin_cannot_promote_nonexistent_user(admin_sandbox):
    client, _ = admin_sandbox
    authenticate_as("admin")

    response = client.post(
        "/admin/organizations",
        json={"email": "inexistente@example.com"},
    )

    assert response.status_code == 404



@pytest.mark.parametrize(
    ("role", "is_active"),
    [
        ("familia", False),
        ("admin", True),
        ("organizacion", True),
    ],
)
def test_admin_cannot_promote_ineligible_account(
    admin_sandbox,
    role,
    is_active,
):
    client, engine = admin_sandbox
    email = f"objetivo-{role}-{str(is_active).lower()}@example.com"

    # 1. Crear el usuario antes de comprobar que existe.
    with Session(engine) as db:
        target = create_user(
            db,
            email=email,
            role=role,
            is_active=is_active,
        )
        target_id = target.id
        original_role = target.role

    # 2. Verificar que sí quedó guardado.
    with Session(engine) as db:
        saved_user = db.scalar(
            select(User).where(User.email == email)
        )
        assert saved_user is not None, f"No se guardó el usuario: {email}"

    # 3. Simular un administrador autenticado y hacer la petición.
    authenticate_as("admin")

    response = client.post(
        "/admin/organizations",
        json={"email": email},
    )

    assert response.status_code == 409, response.text

    # 4. Comprobar que el rol no cambió.
    with Session(engine) as db:
        target = db.get(User, target_id)
        assert target.role == original_role

def test_list_organizations_requires_admin(admin_sandbox):
    client, _ = admin_sandbox
    authenticate_as("familia")

    response = client.get("/admin/organizations")

    assert response.status_code == 403


@pytest.mark.parametrize(
    "email",
    [
        "correo-invalido",
        "sin-arroba.example.com",
        "usuario@",
        "",
    ],
)
def test_promote_organization_rejects_invalid_email(
    admin_sandbox,
    email,
):
    client, _ = admin_sandbox
    authenticate_as("admin")

    response = client.post(
        "/admin/organizations",
        json={"email": email},
    )

    assert response.status_code == 422, response.text


def test_list_organizations_returns_only_organization_accounts(
    admin_sandbox,
):
    client, engine = admin_sandbox

    with Session(engine) as db:
        create_user(
            db,
            email="familia@example.com",
            role="familia",
        )
        create_user(
            db,
            email="organizacion@example.com",
            role="organizacion",
        )
        create_user(
            db,
            email="admin-lista@example.com",
            role="admin",
        )

    authenticate_as("admin")

    response = client.get("/admin/organizations")

    assert response.status_code == 200, response.text
    data = response.json()

    assert len(data) == 1
    assert data[0]["email"] == "organizacion@example.com"
    assert data[0]["role"] == "organizacion"
