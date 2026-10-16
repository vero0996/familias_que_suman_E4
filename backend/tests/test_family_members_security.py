
from datetime import date, timedelta
from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session, sessionmaker
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Family, FamilyMember, User


@pytest.fixture
def member_sandbox():
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

    with TestSession() as db:
        user_a = User(
            full_name="Familia A",
            email="family-a-members@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        user_b = User(
            full_name="Familia B",
            email="family-b-members@test.invalid",
            password_hash="test-only-hash",
            role="familia",
            is_active=True,
        )
        db.add_all([user_a, user_b])
        db.flush()

        family_a = Family(name="Familia A", owner_user_id=user_a.id)
        family_b = Family(name="Familia B", owner_user_id=user_b.id)
        db.add_all([family_a, family_b])
        db.flush()

        own_member = FamilyMember(
            family_id=family_a.id,
            full_name="Integrante propio",
            date_of_birth=date(2010, 5, 10),
            age=16,
            relationship_type="Hijo",
            is_active=True,
        )
        foreign_member = FamilyMember(
            family_id=family_b.id,
            full_name="Integrante ajeno",
            date_of_birth=date(2012, 3, 20),
            age=14,
            relationship_type="Hijo",
            is_active=True,
        )
        inactive_member = FamilyMember(
            family_id=family_a.id,
            full_name="Integrante inactivo",
            date_of_birth=date(2011, 1, 15),
            age=15,
            relationship_type="Hijo",
            is_active=False,
        )
        db.add_all([own_member, foreign_member, inactive_member])
        db.commit()

        ids = {
            "user_a": user_a.id,
            "own_member": own_member.id,
            "foreign_member": foreign_member.id,
            "inactive_member": inactive_member.id,
        }

    identity = {"id": ids["user_a"], "role": "familia"}

    def override_db():
        with TestSession() as db:
            yield db

    def override_user():
        return SimpleNamespace(
            id=identity["id"],
            role=identity["role"],
            is_active=True,
        )

    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides[get_current_user] = override_user

    try:
        with TestClient(app) as client:
            yield client, TestSession, ids, identity
    finally:
        app.dependency_overrides.clear()
        Base.metadata.drop_all(bind=engine)
        engine.dispose()


def valid_update_payload():
    return {
        "full_name": "Nombre actualizado",
        "date_of_birth": "2010-05-10",
        "relationship_type": "Hijo",
    }


def test_family_can_list_only_own_members(member_sandbox):
    client, _, ids, _ = member_sandbox

    response = client.get("/family-members/me")

    assert response.status_code == 200
    returned_ids = {member["id"] for member in response.json()}

    assert ids["own_member"] in returned_ids
    assert ids["foreign_member"] not in returned_ids


def test_family_cannot_update_foreign_member(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.put(
        f"/family-members/{ids['foreign_member']}",
        json=valid_update_payload(),
    )

    assert response.status_code == 404

    with TestSession() as db:
        member = db.get(FamilyMember, ids["foreign_member"])
        assert member.full_name == "Integrante ajeno"


def test_family_cannot_delete_foreign_member(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.delete(
        f"/family-members/{ids['foreign_member']}"
    )

    assert response.status_code == 404

    with TestSession() as db:
        member = db.get(FamilyMember, ids["foreign_member"])
        assert member.is_active is True


def test_family_cannot_update_inactive_member(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.put(
        f"/family-members/{ids['inactive_member']}",
        json=valid_update_payload(),
    )

    assert response.status_code == 404

    with TestSession() as db:
        member = db.get(FamilyMember, ids["inactive_member"])
        assert member.full_name == "Integrante inactivo"
        assert member.is_active is False


def test_family_cannot_delete_inactive_member_again(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.delete(
        f"/family-members/{ids['inactive_member']}"
    )

    assert response.status_code == 404

    with TestSession() as db:
        member = db.get(FamilyMember, ids["inactive_member"])
        assert member.is_active is False


def test_family_can_update_own_active_member(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.put(
        f"/family-members/{ids['own_member']}",
        json=valid_update_payload(),
    )

    assert response.status_code == 200, response.text
    assert response.json()["full_name"] == "Nombre actualizado"

    with TestSession() as db:
        member = db.get(FamilyMember, ids["own_member"])
        assert member.full_name == "Nombre actualizado"


def test_family_can_deactivate_own_active_member(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.delete(
        f"/family-members/{ids['own_member']}"
    )

    assert response.status_code == 204

    with TestSession() as db:
        member = db.get(FamilyMember, ids["own_member"])
        assert member.is_active is False


def test_family_cannot_create_member_with_future_birth_date(
    member_sandbox,
):
    client, TestSession, _, _ = member_sandbox

    response = client.post(
        "/family-members",
        json={
            "full_name": "Integrante futuro",
            "date_of_birth": (date.today() + timedelta(days=1)).isoformat(),
            "relationship_type": "Hijo",
        },
    )

    assert response.status_code == 422

    with TestSession() as db:
        assert (
            db.query(FamilyMember)
            .filter_by(full_name="Integrante futuro")
            .count()
            == 0
        )

def test_family_can_create_member_for_own_family(member_sandbox):
    client, TestSession, ids, _ = member_sandbox

    response = client.post(
        "/family-members",
        json={
            "full_name": "Nuevo integrante",
            "date_of_birth": "2015-06-12",
            "relationship_type": "Hijo",
        },
    )

    assert response.status_code == 201, response.text
    created_id = response.json()["id"]

    with TestSession() as db:
        member = db.get(FamilyMember, created_id)

        assert member is not None
        assert member.family_id != db.get(
            FamilyMember, ids["foreign_member"]
        ).family_id
        assert member.full_name == "Nuevo integrante"
        assert member.is_active is True