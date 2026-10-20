from datetime import datetime, timedelta, timezone
from types import SimpleNamespace

import auth
import dependencies
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
    return SimpleNamespace(
        id=1,
        role=role,
        is_active=is_active,
        token_version=0,
    )


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


def test_current_user_uses_role_from_database_not_token():
    user = make_user(role="familia")
    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=jwt.encode(
            {
                "sub": str(user.id),
                "role": "admin",
                "iat": datetime.now(timezone.utc),
                "exp": datetime.now(timezone.utc) + timedelta(minutes=10),
            },
            "test-only-secret-key-at-least-32-characters-long",
            algorithm="HS256",
        ),
    )

    current_user = get_current_user(credentials, FakeDB(user))

    assert current_user.role == "familia"

    with pytest.raises(HTTPException) as error:
        require_role("admin")(current_user=current_user)

    assert error.value.status_code == 403


@pytest.mark.parametrize(
    "payload",
    [
        {
            "exp": datetime.now(timezone.utc) + timedelta(minutes=10),
            "iat": datetime.now(timezone.utc),
        },
        {
            "sub": "1",
            "iat": datetime.now(timezone.utc),
        },
        {
            "sub": "1",
            "exp": datetime.now(timezone.utc) + timedelta(minutes=10),
        },
    ],
    ids=["missing-sub", "missing-exp", "missing-iat"],
)
def test_token_missing_required_claim_is_rejected(payload):
    token = jwt.encode(
        payload,
        "test-only-secret-key-at-least-32-characters-long",
        algorithm="HS256",
    )
    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=token,
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user()))

    assert error.value.status_code == 401
    assert error.value.detail == "Token inválido o expirado"


@pytest.mark.parametrize("invalid_sub", ["abc", "1.5", "", "0x10"])
def test_token_with_malformed_subject_is_rejected(invalid_sub):
    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=jwt.encode(
            {
                "sub": invalid_sub,
                "iat": datetime.now(timezone.utc),
                "exp": datetime.now(timezone.utc) + timedelta(minutes=10),
            },
            "test-only-secret-key-at-least-32-characters-long",
            algorithm="HS256",
        ),
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user()))

    assert error.value.status_code == 401
    assert error.value.detail == "Token inválido o expirado"


@pytest.mark.parametrize(
    "secret",
    [None, "", "corto"],
    ids=["missing", "empty", "too-short"],
)
def test_auth_rejects_missing_or_weak_jwt_secret(monkeypatch, secret):
    monkeypatch.setenv("JWT_SECRET_KEY", secret) if secret is not None else (
        monkeypatch.delenv("JWT_SECRET_KEY", raising=False)
    )

    with pytest.raises(HTTPException) as error:
        auth.get_jwt_secret()

    assert error.value.status_code == 503


@pytest.mark.parametrize(
    "secret",
    [None, "", "corto"],
    ids=["missing", "empty", "too-short"],
)
def test_dependencies_reject_missing_or_weak_jwt_secret(
    monkeypatch, secret
):
    if secret is None:
        monkeypatch.delenv("JWT_SECRET_KEY", raising=False)
    else:
        monkeypatch.setenv("JWT_SECRET_KEY", secret)

    with pytest.raises(HTTPException) as error:
        dependencies.get_jwt_secret()

    assert error.value.status_code == 503


def test_jwt_secret_of_at_least_32_characters_is_accepted(monkeypatch):
    secret = "test-only-secret-key-at-least-32-characters-long"
    monkeypatch.setenv("JWT_SECRET_KEY", secret)

    assert auth.get_jwt_secret() == secret
    assert dependencies.get_jwt_secret() == secret


def test_token_signed_with_different_algorithm_is_rejected():
    token = jwt.encode(
        {
            "sub": "1",
            "iat": datetime.now(timezone.utc),
            "exp": datetime.now(timezone.utc) + timedelta(minutes=10),
        },
        "test-only-secret-key-at-least-32-characters-long",
        algorithm="HS384",
    )

    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=token,
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user()))

    assert error.value.status_code == 401
    assert error.value.detail == "Token inválido o expirado"


def test_token_with_modified_signature_is_rejected():
    token = make_token()
    parts = token.split(".")

    # Alterar un carácter de la firma sin cambiar el encabezado ni el payload.
    signature = parts[2]
    replacement = "A" if signature[0] != "A" else "B"
    parts[2] = replacement + signature[1:]
    tampered_token = ".".join(parts)

    credentials = HTTPAuthorizationCredentials(
        scheme="Bearer",
        credentials=tampered_token,
    )

    with pytest.raises(HTTPException) as error:
        get_current_user(credentials, FakeDB(make_user()))

    assert error.value.status_code == 401
    assert error.value.detail == "Token inválido o expirado"