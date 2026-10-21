"""Une las migraciones y agrega la fecha de entrada a lista de espera."""

from alembic import op
import sqlalchemy as sa


revision = "c8d9e0f1a203"
down_revision = ("335183bb1d23", "b7c8d9e0f102")
branch_labels = None
depends_on = None


def upgrade():
    op.add_column(
        "registrations",
        sa.Column(
            "waitlisted_at",
            sa.DateTime(timezone=True),
            nullable=True,
        ),
    )

    # Recuperar la entrada más reciente a espera desde el historial.
    # Si no hay historial, usar la fecha original de inscripción.
    op.execute(
        sa.text(
            """
            UPDATE registrations
            SET waitlisted_at = COALESCE(
                (
                    SELECT MAX(registration_events.created_at)
                    FROM registration_events
                    WHERE registration_events.registration_id =
                          registrations.id
                      AND registration_events.to_status = 'lista_espera'
                ),
                registrations.created_at
            )
            WHERE registrations.status = 'lista_espera'
            """
        )
    )


def downgrade():
    op.drop_column("registrations", "waitlisted_at")