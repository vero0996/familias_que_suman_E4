"""change campaign id to activity id in messages

Revision ID: 3a78f8b0bb71
Revises: c80aedd10196
"""

from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


revision: str = "3a78f8b0bb71"
down_revision: Union[str, Sequence[str], None] = "c80aedd10196"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.drop_constraint(
        "messages_campaign_id_fkey",
        "messages",
        type_="foreignkey",
    )

    op.drop_index(
        "ix_messages_campaign_id",
        table_name="messages",
    )

    op.alter_column(
        "messages",
        "campaign_id",
        new_column_name="activity_id",
        existing_type=sa.Integer(),
    )

    op.create_index(
        "ix_messages_activity_id",
        "messages",
        ["activity_id"],
        unique=False,
    )

    op.create_foreign_key(
        "messages_activity_id_fkey",
        "messages",
        "activities",
        ["activity_id"],
        ["id"],
        ondelete="RESTRICT",
    )


def downgrade() -> None:
    op.drop_constraint(
        "messages_activity_id_fkey",
        "messages",
        type_="foreignkey",
    )

    op.drop_index(
        "ix_messages_activity_id",
        table_name="messages",
    )

    op.alter_column(
        "messages",
        "activity_id",
        new_column_name="campaign_id",
        existing_type=sa.Integer(),
    )

    op.create_index(
        "ix_messages_campaign_id",
        "messages",
        ["campaign_id"],
        unique=False,
    )

    op.create_foreign_key(
        "messages_campaign_id_fkey",
        "messages",
        "campaigns",
        ["campaign_id"],
        ["id"],
        ondelete="RESTRICT",
    )