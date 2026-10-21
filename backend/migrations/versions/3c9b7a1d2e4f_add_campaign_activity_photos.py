"""add photo URLs to campaigns and activities

Revision ID: 3c9b7a1d2e4f
Revises: e4f3114a0970
"""

from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


revision: str = "3c9b7a1d2e4f"
down_revision: Union[str, Sequence[str], None] = "e4f3114a0970"
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.add_column(
        "campaigns",
        sa.Column("photo_url", sa.String(length=500), nullable=True),
    )
    op.add_column(
        "activities",
        sa.Column("photo_url", sa.String(length=500), nullable=True),
    )


def downgrade() -> None:
    op.drop_column("activities", "photo_url")
    op.drop_column("campaigns", "photo_url")
