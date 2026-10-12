
import argparse

from sqlalchemy import select

from database import SessionLocal
from models import User


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Promueve un usuario existente a administrador."
    )
    parser.add_argument(
        "--email",
        required=True,
        help="Correo de la cuenta existente que se promoverá.",
    )
    args = parser.parse_args()

    email = args.email.strip().lower()

    with SessionLocal() as db:
        user = db.scalar(select(User).where(User.email == email))

        if user is None:
            parser.error("No existe una cuenta con ese correo.")

        if not user.is_active:
            parser.error("La cuenta está desactivada.")

        if user.role == "admin":
            print("La cuenta ya tiene el rol admin.")
            return

        user.role = "admin"
        db.commit()

        print(f"Rol admin asignado correctamente a {user.email}.")


if __name__ == "__main__":
    main()
