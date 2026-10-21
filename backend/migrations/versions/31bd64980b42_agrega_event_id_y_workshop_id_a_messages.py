"""agrega event_id y workshop_id a messages

Revision ID: 31bd64980b42
Revises: 83cdb93337d2
Create Date: 2026-10-21 03:28:50.982289

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = "31bd64980b42"
down_revision: Union[str, Sequence[str], None] = "83cdb93337d2"
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.add_column(
        "messages",
        sa.Column("event_id", sa.Integer(), nullable=True),
    )

    op.add_column(
        "messages",
        sa.Column("workshop_id", sa.Integer(), nullable=True),
    )

    op.create_index(
        "ix_messages_event_id",
        "messages",
        ["event_id"],
        unique=False,
    )

    op.create_index(
        "ix_messages_workshop_id",
        "messages",
        ["workshop_id"],
        unique=False,
    )

    op.create_foreign_key(
        "fk_messages_event_id",
        "messages",
        "events",
        ["event_id"],
        ["id"],
        ondelete="RESTRICT",
    )

    op.create_foreign_key(
        "fk_messages_workshop_id",
        "messages",
        "workshops",
        ["workshop_id"],
        ["id"],
        ondelete="RESTRICT",
    )


def downgrade() -> None:
    op.drop_constraint(
        "fk_messages_workshop_id",
        "messages",
        type_="foreignkey",
    )

    op.drop_constraint(
        "fk_messages_event_id",
        "messages",
        type_="foreignkey",
    )

    op.drop_index(
        "ix_messages_workshop_id",
        table_name="messages",
    )

    op.drop_index(
        "ix_messages_event_id",
        table_name="messages",
    )

    op.drop_column(
        "messages",
        "workshop_id",
    )

    op.drop_column(
        "messages",
        "event_id",
    )