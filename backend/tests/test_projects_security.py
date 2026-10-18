from types import SimpleNamespace

import pytest
from fastapi.testclient import TestClient
from sqlalchemy import create_engine
from sqlalchemy.orm import Session
from sqlalchemy.pool import StaticPool

from database import Base, get_db
from dependencies import get_current_user
from main import app
from models import Project


@pytest.fixture
def project_sandbox():
    engine = create_engine(
        "sqlite://",
        connect_args={"check_same_thread": False},
        poolclass=StaticPool,
    )
    Base.metadata.create_all(engine)

    with Session(engine) as db:
        project = Project(
            title="Proyecto de prueba",
            description="Descripción de prueba",
        )
        db.add(project)
        db.commit()
        db.refresh(project)

        project_id = project.id

    def override_db():
        with Session(engine) as session:
            yield session

    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides[get_db] = override_db
    app.dependency_overrides.pop(get_current_user, None)

    try:
        with TestClient(app) as client:
            yield client, engine, project_id
    finally:
        app.dependency_overrides.clear()
        app.dependency_overrides.update(previous_overrides)
        Base.metadata.drop_all(engine)
        engine.dispose()


def authenticate_as(role: str):
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=1,
        role=role,
        is_active=True,
    )


def valid_project_payload():
    return {
        "title": "Nuevo proyecto",
        "description": "Descripción del nuevo proyecto",
    }


def test_projects_are_public(project_sandbox):
    client, _, _ = project_sandbox

    response = client.get("/api/proyectos/")

    assert response.status_code == 200
    assert len(response.json()) == 1


def test_project_can_be_read_by_id(project_sandbox):
    client, _, project_id = project_sandbox

    response = client.get(f"/api/proyectos/{project_id}")

    assert response.status_code == 200
    assert response.json()["id"] == project_id


def test_family_cannot_create_project(project_sandbox):
    client, _, _ = project_sandbox
    authenticate_as("familia")

    response = client.post(
        "/api/proyectos/",
        json=valid_project_payload(),
    )

    assert response.status_code == 403


def test_organization_can_create_project(project_sandbox):
    client, _, _ = project_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/proyectos/",
        json=valid_project_payload(),
    )

    assert response.status_code == 201
    assert response.json()["title"] == "Nuevo proyecto"


def test_admin_can_create_project(project_sandbox):
    client, _, _ = project_sandbox
    authenticate_as("admin")

    response = client.post(
        "/api/proyectos/",
        json=valid_project_payload(),
    )

    assert response.status_code == 201


def test_family_cannot_update_project(project_sandbox):
    client, _, project_id = project_sandbox
    authenticate_as("familia")

    response = client.patch(
        f"/api/proyectos/{project_id}",
        json={"title": "Proyecto cambiado"},
    )

    assert response.status_code == 403


def test_organization_can_update_project(project_sandbox):
    client, _, project_id = project_sandbox
    authenticate_as("organizacion")

    response = client.patch(
        f"/api/proyectos/{project_id}",
        json={"title": "Proyecto actualizado"},
    )

    assert response.status_code == 200
    assert response.json()["title"] == "Proyecto actualizado"


def test_family_cannot_delete_project(project_sandbox):
    client, _, project_id = project_sandbox
    authenticate_as("familia")

    response = client.delete(f"/api/proyectos/{project_id}")

    assert response.status_code == 403


def test_organization_can_delete_project(project_sandbox):
    client, engine, project_id = project_sandbox
    authenticate_as("organizacion")

    response = client.delete(f"/api/proyectos/{project_id}")

    assert response.status_code == 204

    with Session(engine) as db:
        assert db.get(Project, project_id) is None


def test_admin_can_delete_project(project_sandbox):
    client, _, project_id = project_sandbox
    authenticate_as("admin")

    response = client.delete(f"/api/proyectos/{project_id}")

    assert response.status_code == 204


def test_project_not_found_returns_404(project_sandbox):
    client, _, _ = project_sandbox

    response = client.get("/api/proyectos/99999")

    assert response.status_code == 404


def test_invalid_project_is_rejected(project_sandbox):
    client, _, _ = project_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/proyectos/",
        json={
            "title": "",
            "description": "",
        },
    )

    assert response.status_code == 422