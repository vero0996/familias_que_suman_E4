
import re

from pydantic import BaseModel, ConfigDict, Field, field_validator
from datetime import datetime
from typing import Literal

from pydantic import BaseModel, ConfigDict, Field, field_validator
from datetime import datetime
from typing import Literal
from pydantic import AliasChoices
from pydantic import model_validator


ActivityStatus = Literal["borrador", "publicada", "cancelada", "finalizada"]
CampaignStatus = Literal["borrador", "publicada", "cancelada", "finalizada"]


class RegisterRequest(BaseModel):
    full_name: str = Field(min_length=2, max_length=150)
    email: str = Field(min_length=5, max_length=255)
    phone: str | None = Field(default=None, max_length=30)
    password: str = Field(min_length=8, max_length=128)
    family_name: str = Field(min_length=2, max_length=150)

    @field_validator("full_name", "family_name")
    @classmethod
    def clean_names(cls, value: str) -> str:
        value = value.strip()
        if len(value) < 2:
            raise ValueError("El nombre debe tener al menos 2 caracteres")
        return value

    @field_validator("email")
    @classmethod
    def normalize_email(cls, value: str) -> str:
        value = value.strip().lower()
        if not re.fullmatch(r"[^@\s]+@[^@\s]+\.[^@\s]+", value):
            raise ValueError("Correo electrónico no válido")
        return value

    @field_validator("phone")
    @classmethod
    def clean_phone(cls, value: str | None) -> str | None:
        if value is None:
            return None
        value = value.strip()
        return value or None


class LoginRequest(BaseModel):
    email: str = Field(min_length=5, max_length=255)
    password: str = Field(min_length=1, max_length=128)

    @field_validator("email")
    @classmethod
    def normalize_email(cls, value: str) -> str:
        value = value.strip().lower()
        if not re.fullmatch(r"[^@\s]+@[^@\s]+\.[^@\s]+", value):
            raise ValueError("Correo electrónico no válido")
        return value


class RegisterResponse(BaseModel):
    id: int
    full_name: str
    email: str
    phone: str | None
    role: str
    family_id: int
    family_name: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"


class CurrentUserResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    full_name: str
    email: str
    phone: str | None
    role: str


class CampaignCreate(BaseModel):
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(min_length=1, max_length=5000)
    organization_name: str = Field(min_length=2, max_length=200)

    @field_validator("title", "description", "organization_name")
    @classmethod
    def clean_text(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value


class CampaignUpdate(BaseModel):
    title: str | None = Field(default=None, min_length=3, max_length=200)
    description: str | None = Field(default=None, min_length=1, max_length=5000)
    organization_name: str | None = Field(default=None, min_length=2, max_length=200)
    status: CampaignStatus | None = None

    @field_validator("title", "description", "organization_name")
    @classmethod
    def clean_optional_text(cls, value: str | None) -> str | None:
        if value is None:
            return None
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value


class CampaignResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    title: str
    description: str
    organization_name: str
    status: str
    created_by_user_id: int | None
    created_at: datetime
    updated_at: datetime


class OrganizationPromoteRequest(BaseModel):
    email: str = Field(min_length=5, max_length=255)

    @field_validator("email")
    @classmethod
    def normalize_email(cls, value: str) -> str:
        value = value.strip().lower()
        if not re.fullmatch(r"[^@\s]+@[^@\s]+\.[^@\s]+", value):
            raise ValueError("Correo electrónico no válido")
        return value


class OrganizationAdminResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    full_name: str
    email: str
    phone: str | None
    role: str
    is_active: bool
    created_at: datetime

class ActivityCreate(BaseModel):
    campaign_id: int = Field(gt=0)
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(min_length=1, max_length=5000)
    starts_at: datetime
    ends_at: datetime
    location: str = Field(min_length=2, max_length=300)
    capacity_people: int = Field(gt=0)
    registration_deadline: datetime | None = None

    @field_validator("title", "description", "location")
    @classmethod
    def clean_activity_text(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value

    @model_validator(mode="after")
    def validate_dates(self):
        if self.ends_at <= self.starts_at:
            raise ValueError("La fecha de finalización debe ser posterior al inicio")

        if (
            self.registration_deadline is not None
            and self.registration_deadline > self.starts_at
        ):
            raise ValueError(
                "La fecha límite de inscripción no puede ser posterior al inicio"
            )

        return self


class ActivityUpdate(BaseModel):
    title: str | None = Field(default=None, min_length=3, max_length=200)
    description: str | None = Field(default=None, min_length=1, max_length=5000)
    starts_at: datetime | None = None
    ends_at: datetime | None = None
    location: str | None = Field(default=None, min_length=2, max_length=300)
    capacity_people: int | None = Field(default=None, gt=0)
    registration_deadline: datetime | None = None
    status: ActivityStatus | None = None

    @field_validator("title", "description", "location")
    @classmethod
    def clean_optional_activity_text(cls, value: str | None) -> str | None:
        if value is None:
            return None
        value = value.strip()
        if not value:
            raise ValueError("Este campo no puede estar vacío")
        return value



class ActivityResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    campaign_id: int | None
    title: str
    description: str
    starts_at: datetime
    ends_at: datetime | None
    location: str
    capacity_people: int = Field(
        validation_alias=AliasChoices(
            "capacity_people",
            "capacity_families",
        )
    )
    registration_deadline: datetime | None
    status: str
    created_by_user_id: int | None
    created_at: datetime
    confirmed_people: int = 0



class RegistrationCreate(BaseModel):
    activity_id: int = Field(gt=0)
    participant_ids: list[int] = Field(
        default_factory=list,
        min_length=1,
        max_length=20,
    )
    notes: str | None = Field(default=None, max_length=2000)

    @field_validator("participant_ids")
    @classmethod
    def validate_participants(cls, value: list[int]) -> list[int]:
        if any(participant_id <= 0 for participant_id in value):
            raise ValueError("Los IDs de integrantes deben ser positivos")

        if len(value) != len(set(value)):
            raise ValueError("No se permiten integrantes duplicados")

        return value

    @field_validator("notes")
    @classmethod
    def clean_notes(cls, value: str | None) -> str | None:
        if value is None:
            return None

        value = value.strip()
        return value or None



class RegistrationCancelRequest(BaseModel):
    reason: str | None = Field(default=None, max_length=500)

    @field_validator("reason")
    @classmethod
    def clean_reason(cls, value: str | None) -> str | None:
        if value is None:
            return None

        value = value.strip()
        return value or None


class RegistrationResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    activity_id: int
    family_id: int
    status: str
    notes: str | None
    created_at: datetime
    updated_at: datetime
    canceled_at: datetime | None


class SurveyCreate(BaseModel):
    registration_id: int = Field(gt=0)
    campaign_rating: int = Field(ge=1, le=5)
    organization_rating: int = Field(ge=1, le=5)
    communication_rating: int = Field(ge=1, le=5)
    overall_rating: int = Field(ge=1, le=5)
    comment: str | None = Field(default=None, max_length=5000)
    photo_url: str | None = Field(default=None, max_length=500)
    photo_consent: bool = False

    @field_validator("comment")
    @classmethod
    def clean_comment(cls, value: str | None) -> str | None:
        if value is None:
            return None
        value = value.strip()
        return value or None


class SurveyResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)

    id: int
    registration_id: int
    campaign_rating: int
    organization_rating: int
    communication_rating: int
    overall_rating: int
    comment: str | None
    photo_url: str | None
    photo_consent: bool
    created_at: datetime

class ProjectCreate(BaseModel):
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(min_length=1, max_length=5000)

class ProjectResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    title: str
    description: str
    created_at: datetime

class EventCreate(BaseModel):
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(min_length=1, max_length=5000)
    location: str = Field(min_length=2, max_length=300)
    starts_at: datetime

class EventResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    title: str
    description: str
    location: str
    starts_at: datetime
    created_at: datetime

class WorkshopCreate(BaseModel):
    title: str = Field(min_length=3, max_length=200)
    description: str = Field(min_length=1, max_length=5000)
    location: str = Field(min_length=2, max_length=300)
    starts_at: datetime

class WorkshopResponse(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    id: int
    title: str
    description: str
    location: str
    starts_at: datetime
    created_at: datetime
