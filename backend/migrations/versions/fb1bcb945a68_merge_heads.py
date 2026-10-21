"""merge heads

Revision ID: fb1bcb945a68
Revises: 335183bb1d23, b7c8d9e0f102
"""

from typing import Sequence, Union


revision: str = "fb1bcb945a68"
down_revision: Union[str, Sequence[str], None] = (
    "335183bb1d23",
    "b7c8d9e0f102",
)
branch_labels = None
depends_on = None


def upgrade() -> None:
    pass


def downgrade() -> None:
    pass