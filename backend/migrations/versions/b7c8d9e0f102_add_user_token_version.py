"""Agrega versión de sesión para invalidar tokens anteriores."""

from alembic import op
import sqlalchemy as sa


revision = "b7c8d9e0f102"
down_revision = "f4b5c6d7e801"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.add_column(
        "users",
        sa.Column(
            "token_version",
            sa.Integer(),
            server_default=sa.text("0"),
            nullable=False,
        ),
    )


def downgrade() -> None:
    op.drop_column("users", "token_version")