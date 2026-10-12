
from datetime import datetime, timezone

from sqlalchemy import (
    Boolean,
    CheckConstraint,
    DateTime,
    ForeignKey,
    Integer,
    String,
    Text,
    UniqueConstraint,
    Table,
    Column,
)
from sqlalchemy.orm import Mapped, mapped_column, relationship

from database import Base


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


registration_participants = Table(
    "registration_participants",
    Base.metadata,
    Column(
        "registration_id",
        Integer,
        ForeignKey("registrations.id", ondelete="RESTRICT"),
        primary_key=True,
    ),
    Column(
        "family_member_id",
        Integer,
        ForeignKey("family_members.id", ondelete="RESTRICT"),
        primary_key=True,
    ),
)


class User(Base):
    __tablename__ = "users"
    __table_args__ = (
        CheckConstraint(
            "role IN ('familia', 'admin', 'organizacion')",
            name="ck_users_role",
        ),
    )

    id: Mapped[int] = mapped_column(primary_key=True)
    full_name: Mapped[str] = mapped_column(String(150))
    email: Mapped[str] = mapped_column(String(255), unique=True, index=True)
    phone: Mapped[str | None] = mapped_column(String(30), nullable=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    role: Mapped[str] = mapped_column(String(20), default="familia")
    is_active: Mapped[bool] = mapped_column(Boolean, default=True)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )

    family: Mapped["Family | None"] = relationship(
        back_populates="owner", uselist=False
    )


class Family(Base):
    __tablename__ = "families"

    id: Mapped[int] = mapped_column(primary_key=True)
    name: Mapped[str] = mapped_column(String(150))
    owner_user_id: Mapped[int] = mapped_column(
        ForeignKey("users.id", ondelete="RESTRICT"),
        unique=True,
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )

    owner: Mapped["User"] = relationship(back_populates="family")
    members: Mapped[list["FamilyMember"]] = relationship(
        back_populates="family"
    )


class FamilyMember(Base):
    __tablename__ = "family_members"
    __table_args__ = (
        CheckConstraint("age IS NULL OR age >= 0", name="ck_member_age"),
    )

    id: Mapped[int] = mapped_column(primary_key=True)
    family_id: Mapped[int] = mapped_column(
        ForeignKey("families.id", ondelete="RESTRICT"), index=True
    )
    full_name: Mapped[str] = mapped_column(String(150))
    age: Mapped[int | None] = mapped_column(Integer, nullable=True)
    relationship_type: Mapped[str] = mapped_column(String(50))
    is_active: Mapped[bool] = mapped_column(Boolean, default=True)

    family: Mapped["Family"] = relationship(back_populates="members")


class Campaign(Base):
    __tablename__ = "campaigns"

    id: Mapped[int] = mapped_column(primary_key=True)
    title: Mapped[str] = mapped_column(String(200))
    description: Mapped[str] = mapped_column(Text, default="")
    organization_name: Mapped[str] = mapped_column(String(200))
    status: Mapped[str] = mapped_column(String(20), default="borrador")
    created_by_user_id: Mapped[int | None] = mapped_column(
        ForeignKey("users.id", ondelete="RESTRICT"), nullable=True
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now
    )

    activities: Mapped[list["Activity"]] = relationship(
        back_populates="campaign"
    )


class Activity(Base):
    __tablename__ = "activities"
    __table_args__ = (
        CheckConstraint(
            "capacity_families > 0", name="ck_activity_capacity"
        ),
        CheckConstraint(
            "status IN ('borrador', 'publicada', 'cancelada', 'finalizada')",
            name="ck_activity_status",
        ),
    )

    id: Mapped[int] = mapped_column(primary_key=True)
    campaign_id: Mapped[int | None] = mapped_column(
        ForeignKey("campaigns.id", ondelete="RESTRICT"),
        nullable=True,
        index=True,
    )
    title: Mapped[str] = mapped_column(String(200))
    description: Mapped[str] = mapped_column(Text, default="")
    starts_at: Mapped[datetime] = mapped_column(DateTime(timezone=True))
    ends_at: Mapped[datetime | None] = mapped_column(
        DateTime(timezone=True), nullable=True
    )
    location: Mapped[str] = mapped_column(String(250))
    capacity_families: Mapped[int] = mapped_column(Integer)
    registration_deadline: Mapped[datetime] = mapped_column(
        DateTime(timezone=True)
    )
    status: Mapped[str] = mapped_column(String(20), default="borrador")
    created_by_user_id: Mapped[int | None] = mapped_column(
        ForeignKey("users.id", ondelete="RESTRICT"), nullable=True
    )
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )

    campaign: Mapped["Campaign | None"] = relationship(
        back_populates="activities"
    )
    registrations: Mapped[list["Registration"]] = relationship(
        back_populates="activity"
    )


class Registration(Base):
    __tablename__ = "registrations"
    __table_args__ = (
        UniqueConstraint(
            "activity_id", "family_id", name="uq_registration_activity_family"
        ),
        CheckConstraint(
            "status IN ('confirmada', 'lista_espera', 'cancelada')",
            name="ck_registration_status",
        ),
    )

    id: Mapped[int] = mapped_column(primary_key=True)
    activity_id: Mapped[int] = mapped_column(
        ForeignKey("activities.id", ondelete="RESTRICT"), index=True
    )
    family_id: Mapped[int] = mapped_column(
        ForeignKey("families.id", ondelete="RESTRICT"), index=True
    )
    status: Mapped[str] = mapped_column(
        String(20), default="confirmada", index=True
    )
    notes: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )
    updated_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now, onupdate=utc_now
    )
    canceled_at: Mapped[datetime | None] = mapped_column(
        DateTime(timezone=True), nullable=True
    )

    activity: Mapped["Activity"] = relationship(
        back_populates="registrations"
    )
    participants: Mapped[list["FamilyMember"]] = relationship(
        secondary=registration_participants
    )
    events: Mapped[list["RegistrationEvent"]] = relationship(
        back_populates="registration"
    )
    survey: Mapped["Survey | None"] = relationship(
        back_populates="registration", uselist=False
    )


class RegistrationEvent(Base):
    __tablename__ = "registration_events"

    id: Mapped[int] = mapped_column(primary_key=True)
    registration_id: Mapped[int] = mapped_column(
        ForeignKey("registrations.id", ondelete="RESTRICT"), index=True
    )
    event_type: Mapped[str] = mapped_column(String(40))
    from_status: Mapped[str | None] = mapped_column(
        String(20), nullable=True
    )
    to_status: Mapped[str | None] = mapped_column(
        String(20), nullable=True
    )
    actor_user_id: Mapped[int | None] = mapped_column(
        ForeignKey("users.id", ondelete="RESTRICT"), nullable=True
    )
    details: Mapped[str | None] = mapped_column(Text, nullable=True)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )

    registration: Mapped["Registration"] = relationship(
        back_populates="events"
    )


class Survey(Base):
    __tablename__ = "surveys"
    __table_args__ = (
        UniqueConstraint("registration_id", name="uq_survey_registration"),
        CheckConstraint(
            "campaign_rating BETWEEN 1 AND 5",
            name="ck_survey_campaign_rating",
        ),
        CheckConstraint(
            "organization_rating BETWEEN 1 AND 5",
            name="ck_survey_organization_rating",
        ),
        CheckConstraint(
            "communication_rating BETWEEN 1 AND 5",
            name="ck_survey_communication_rating",
        ),
        CheckConstraint(
            "overall_rating BETWEEN 1 AND 5",
            name="ck_survey_overall_rating",
        ),
    )

    id: Mapped[int] = mapped_column(primary_key=True)
    registration_id: Mapped[int] = mapped_column(
        ForeignKey("registrations.id", ondelete="RESTRICT")
    )
    campaign_rating: Mapped[int] = mapped_column(Integer)
    organization_rating: Mapped[int] = mapped_column(Integer)
    communication_rating: Mapped[int] = mapped_column(Integer)
    overall_rating: Mapped[int] = mapped_column(Integer)
    comment: Mapped[str | None] = mapped_column(Text, nullable=True)
    photo_url: Mapped[str | None] = mapped_column(String(500), nullable=True)
    photo_consent: Mapped[bool] = mapped_column(Boolean, default=False)
    created_at: Mapped[datetime] = mapped_column(
        DateTime(timezone=True), default=utc_now
    )

    registration: Mapped["Registration"] = relationship(
        back_populates="survey"
    )
