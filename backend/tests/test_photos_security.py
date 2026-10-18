from types import SimpleNamespace
import os

import pytest
from fastapi.testclient import TestClient

from dependencies import get_current_user
from main import app


@pytest.fixture
def photo_sandbox():
    previous_overrides = app.dependency_overrides.copy()
    app.dependency_overrides.pop(get_current_user, None)

    try:
        with TestClient(app) as client:
            yield client
    finally:
        app.dependency_overrides.clear()
        app.dependency_overrides.update(previous_overrides)


def authenticate_as(role: str):
    app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
        id=1,
        role=role,
        is_active=True,
    )


def delete_test_file(file_name: str):
    file_location = f"uploads/{file_name}"

    if os.path.exists(file_location):
        os.remove(file_location)


def test_photo_upload_requires_authentication(photo_sandbox):
    client = photo_sandbox

    response = client.post(
        "/api/fotos/",
        files={
            "file": (
                "foto.jpg",
                b"imagen-de-prueba",
                "image/jpeg",
            )
        },
    )

    assert response.status_code == 401


def test_family_can_upload_jpg(photo_sandbox):
    client = photo_sandbox
    authenticate_as("familia")

    file_name = "familia.jpg"
    delete_test_file(file_name)

    response = client.post(
        "/api/fotos/",
        files={
            "file": (
                file_name,
                b"imagen-de-prueba",
                "image/jpeg",
            )
        },
    )

    assert response.status_code == 201
    assert response.json()["mensaje"] == "Foto subida"
    assert response.json()["url"] == "/uploads/familia.jpg"
    assert os.path.exists("uploads/familia.jpg")

    delete_test_file(file_name)


def test_organization_can_upload_png(photo_sandbox):
    client = photo_sandbox
    authenticate_as("organizacion")

    file_name = "organizacion.png"
    delete_test_file(file_name)

    response = client.post(
        "/api/fotos/",
        files={
            "file": (
                file_name,
                b"imagen-de-prueba",
                "image/png",
            )
        },
    )

    assert response.status_code == 201
    assert os.path.exists("uploads/organizacion.png")

    delete_test_file(file_name)


def test_admin_can_upload_webp(photo_sandbox):
    client = photo_sandbox
    authenticate_as("admin")

    file_name = "admin.webp"
    delete_test_file(file_name)

    response = client.post(
        "/api/fotos/",
        files={
            "file": (
                file_name,
                b"imagen-de-prueba",
                "image/webp",
            )
        },
    )

    assert response.status_code == 201
    assert os.path.exists("uploads/admin.webp")

    delete_test_file(file_name)


def test_text_file_is_rejected(photo_sandbox):
    client = photo_sandbox
    authenticate_as("organizacion")

    response = client.post(
        "/api/fotos/",
        files={
            "file": (
                "archivo.txt",
                b"esto no es una imagen",
                "text/plain",
            )
        },
    )

    assert response.status_code == 422
    assert not os.path.exists("uploads/archivo.txt")


def test_pdf_file_is_rejected(photo_sandbox):
    client = photo_sandbox
    authenticate_as("admin")

    response = client.post(
        "/api/fotos/",
        files={
            "file": (
                "archivo.pdf",
                b"archivo-de-prueba",
                "application/pdf",
            )
        },
    )

    assert response.status_code == 422
    assert not os.path.exists("uploads/archivo.pdf")