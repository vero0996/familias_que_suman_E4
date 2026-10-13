
from types import SimpleNamespace

import pytest

from database import get_db
from dependencies import get_current_user
from main import app


@pytest.fixture
def override_user():
    def apply(role: str):
        app.dependency_overrides[get_current_user] = lambda: SimpleNamespace(
            id=1,
            role=role,
            is_active=True,
        )

        def fake_db():
            yield object()

        app.dependency_overrides[get_db] = fake_db

    yield apply
    app.dependency_overrides.clear()


@pytest.mark.parametrize(
    ("path", "role"),
    [
        ("/admin/organizations", "familia"),
        ("/admin/organizations", "organizacion"),
        ("/family-members/me", "admin"),
        ("/family-members/me", "organizacion"),
        ("/registrations/me", "admin"),
        ("/registrations/me", "organizacion"),
    ],
)
def test_wrong_role_is_forbidden(client, override_user, path, role):
    override_user(role)

    response = client.get(path)

    assert response.status_code == 403, (
        f"Se esperaba 403 para el rol '{role}' en {path}, "
        f"pero se recibió {response.status_code}"
    )
