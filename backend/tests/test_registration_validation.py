
import pytest
from pydantic import ValidationError

from schemas import RegistrationCreate


@pytest.mark.parametrize(
    "payload",
    [
        {"activity_id": 1, "participant_ids": []},
        {"activity_id": 1, "participant_ids": [1, 1]},
        {"activity_id": 1, "participant_ids": [0]},
        {"activity_id": 1, "participant_ids": [-1]},
        {"activity_id": 1, "participant_ids": list(range(1, 22))},
        {"activity_id": 0, "participant_ids": [1]},
        {"activity_id": -1, "participant_ids": [1]},
    ],
)
def test_invalid_registration_payload_is_rejected(payload):
    with pytest.raises(ValidationError):
        RegistrationCreate.model_validate(payload)


def test_valid_registration_payload_is_accepted():
    data = RegistrationCreate.model_validate(
        {
            "activity_id": 1,
            "participant_ids": [1, 2],
            "notes": "  Necesitamos acceso accesible  ",
        }
    )

    assert data.activity_id == 1
    assert data.participant_ids == [1, 2]
    assert data.notes == "Necesitamos acceso accesible"


def test_blank_notes_are_normalized_to_none():
    data = RegistrationCreate.model_validate(
        {
            "activity_id": 1,
            "participant_ids": [1],
            "notes": "   ",
        }
    )

    assert data.notes is None
