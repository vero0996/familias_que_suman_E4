from datetime import datetime, timedelta, timezone
from types import SimpleNamespace

import jwt
import pytest
from fastapi import HTTPException
from fastapi.security import HTTPAuthorizationCredentials

from dependencies import get_current_user, require_role


class FakeDB:
    def __init__(self, user=None):
        self.user = user

    def get(self, model, user_id):
        return self.user if self.user and self.user.id == user_id else None


def make_user(role="familia", is_active=True):
    return SimpleNamespace(id=1, role=role, is_active=is_active)


def make_token(user_id="1", expires_delta=timedelta(minutes=10)):
    now = datetime.now(timezone.utc)
    return jwt.encode(
        {
            "sub": user_id,
            "iat": now,
            "exp": now + expires_delta,
        },
        "test-only-secret-key-at-least-32-characters-long",
        algorithm="HS256",
    )


def test_protected_routes_reject_missing_token(client):
    for path in ("/registrations/me", "/admin/organizations"):
        response = client.get(path)
        assert response.status_code == 401, path


def test_expired_token_is_rejected():
    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=make_token(expires_delta=timedelta(seconds=-10)),
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user()))

    assert error.value.status_code == 401


def test_invalid_token_is_rejected():
    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials="this-is-not-a-valid-token",
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user()))

    assert error.value.status_code == 401


def test_inactive_user_is_rejected():
    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=make_token(),
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user(is_active=False)))

    assert error.value.status_code == 401


def test_family_role_cannot_access_admin_function():
    checker = require_role("admin")

    with pytest.raises(HTTPException) as error:
        checker(current_user=make_user(role="familia"))

    assert error.value.status_code == 403


def test_admin_role_can_access_admin_function():
    checker = require_role("admin")
    user = make_user(role="admin")

    assert checker(current_user=user) is user


def test_invalid_role_configuration_is_rejected():
    with pytest.raises(ValueError):
        require_role("superuser")
