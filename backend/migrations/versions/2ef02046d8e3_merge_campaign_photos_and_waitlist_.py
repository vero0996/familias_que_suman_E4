"""merge campaign photos and waitlist migrations

Revision ID: 2ef02046d8e3
Revises: 3c9b7a1d2e4f, c8d9e0f1a203
Create Date: 2026-10-21 07:34:02.363099

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '2ef02046d8e3'
down_revision: Union[str, Sequence[str], None] = ('3c9b7a1d2e4f', 'c8d9e0f1a203')
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    """Upgrade schema."""
    pass


def downgrade() -> None:
    """Downgrade schema."""
    pass
