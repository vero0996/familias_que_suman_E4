import os
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timedelta, timezone
from threading import Barrier
from types import SimpleNamespace

import pytest
from fastapi import HTTPException
from sqlalchemy import create_engine, select
from sqlalchemy.engine import make_url
from sqlalchemy.orm import Session

from database import Base
from models import Activity, Campaign, Family, FamilyMember, Registration, User
from registrations import create_registration
from schemas import RegistrationCreate


@pytest.fixture
def postgres_registrations():
    database_url = os.getenv("TEST_DATABASE_URL")
    if not database_url:
        pytest.skip("Se requiere TEST_DATABASE_URL para estas pruebas")

    url = make_url(database_url)
    if (
        url.get_backend_name() != "postgresql"
        or url.host != "db-test"
        or url.database != "familias_test"
    ):
        pytest.fail("Estas pruebas requieren la base exclusiva db-test/familias_test")

    engine = create_engine(database_url, pool_pre_ping=True)
    Base.metadata.create_all(engine)

    try:
        with Session(engine) as db:
            now = datetime.now(timezone.utc)
            campaign = Campaign(
                title="Campaña de concurrencia",
                description="Datos ficticios",
                organization_name="Organización de prueba",
                status="publicada",
            )
            db.add(campaign)
            db.flush()

            activity = Activity(
                campaign_id=campaign.id,
                title="Últimos dos lugares",
                description="Prueba",
                starts_at=now + timedelta(days=10),
                registration_deadline=now + timedelta(days=5),
                location="Lugar de prueba",
                capacity_people=2,
                status="publicada",
            )
            db.add(activity)

            families = []
            for index in range(2):
                user = User(
                    full_name=f"Usuario {index}",
                    email=f"concurrencia-{index}@test.invalid",
                    password_hash="test-only-hash",
                    role="familia",
                    is_active=True,
                )
                db.add(user)
                db.flush()

                family = Family(
                    name=f"Familia {index}",
                    owner_user_id=user.id,
                )
                db.add(family)
                db.flush()

                member = FamilyMember(
                    family_id=family.id,
                    full_name=f"Acompañante {index}",
                    age=10,
                    relationship_type="Hijo",
                    is_active=True,
                )
                db.add(member)
                db.flush()
                families.append((user.id, member.id))

            db.commit()
            activity_id = activity.id

        yield engine, activity_id, families

    finally:
        Base.metadata.drop_all(engine)
        engine.dispose()


def register_simultaneously(engine, activity_id, applicants):
    barrier = Barrier(len(applicants))

    def attempt(applicant):
        user_id, member_id = applicant
        with Session(
            engine,
            autoflush=False,
            expire_on_commit=False,
        ) as db:
            request = RegistrationCreate(
                activity_id=activity_id,
                participant_ids=[member_id],
            )
            user = SimpleNamespace(
                id=user_id,
                role="familia",
                is_active=True,
            )

            barrier.wait(timeout=10)

            try:
                registration = create_registration(
                    data=request,
                    db=db,
                    current_user=user,
                )
                return 201, registration.status
            except HTTPException as error:
                db.rollback()
                return error.status_code, None

    with ThreadPoolExecutor(max_workers=len(applicants)) as executor:
        return list(executor.map(attempt, applicants))


def test_two_families_cannot_exceed_capacity(postgres_registrations):
    engine, activity_id, families = postgres_registrations

    results = register_simultaneously(engine, activity_id, families)

    assert sorted(results) == [
        (201, "confirmada"),
        (201, "lista_espera"),
    ]

    with Session(engine) as db:
        registrations = db.scalars(
            select(Registration).where(
                Registration.activity_id == activity_id
            )
        ).all()

        assert len(registrations) == 2
        occupied = sum(
            1 + len(item.participants)
            for item in registrations
            if item.status == "confirmada"
        )
        assert occupied == 2
        assert occupied <= db.get(Activity, activity_id).capacity_people


def test_same_family_cannot_register_twice_simultaneously(
    postgres_registrations,
):
    engine, activity_id, families = postgres_registrations

    results = register_simultaneously(
        engine,
        activity_id,
        [families[0], families[0]],
    )

    assert sorted(code for code, _ in results) == [201, 409]

    with Session(engine) as db:
        registrations = db.scalars(
            select(Registration).where(
                Registration.activity_id == activity_id
            )
        ).all()

        assert len(registrations) == 1
        assert registrations[0].status == "confirmada"
        assert len(registrations[0].participants) == 1