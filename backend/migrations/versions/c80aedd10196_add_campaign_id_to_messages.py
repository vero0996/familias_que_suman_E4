"""add campaign id to messages

Revision ID: c80aedd10196
Revises: fb1bcb945a68
"""

from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


revision: str = "c80aedd10196"
down_revision: Union[str, Sequence[str], None] = "fb1bcb945a68"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.add_column(
        "messages",
        sa.Column("campaign_id", sa.Integer(), nullable=True),
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


def downgrade() -> None:
    op.drop_constraint(
        "messages_campaign_id_fkey",
        "messages",
        type_="foreignkey",
    )
    op.drop_index(
        "ix_messages_campaign_id",
        table_name="messages",
    )
    op.drop_column(
        "messages",
        "campaign_id",
    )