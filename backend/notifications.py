from datetime import datetime, timedelta, timezone

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy import select
from sqlalchemy.orm import Session

from database import get_db
from dependencies import get_current_user
from models import Activity, Family, Notification, Registration, User
from schemas import NotificationResponse


router = APIRouter(
    prefix="/api/notificaciones",
    tags=["Notificaciones"]
)


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


def ensure_utc(value: datetime) -> datetime:
    if value.tzinfo is None:
        return value.replace(tzinfo=timezone.utc)

    return value.astimezone(timezone.utc)


def notification_exists(
    db: Session,
    user_id: int,
    notification_type: str,
) -> bool:
    notification = db.scalar(
        select(Notification).where(
            Notification.user_id == user_id,
            Notification.notification_type == notification_type,
        )
    )

    return notification is not None


def create_reminders(
    db: Session,
    current_user: User,
) -> None:
    if current_user.role != "familia":
        return

    family = db.scalar(
        select(Family).where(
            Family.owner_user_id == current_user.id
        )
    )

    if family is None:
        return

    registrations = db.scalars(
        select(Registration).where(
            Registration.family_id == family.id,
            Registration.status == "confirmada",
        )
    ).all()

    now = utc_now()

    for registration in registrations:
        activity = db.get(Activity, registration.activity_id)

        if activity is None:
            continue

        starts_at = ensure_utc(activity.starts_at)

        if starts_at <= now:
            continue

        remaining = starts_at - now

        reminder_24h_type = f"activity_24h_{activity.id}"
        reminder_today_type = f"activity_today_{activity.id}"

        if (
            timedelta(0) < remaining <= timedelta(hours=24)
            and starts_at.date() != now.date()
            and not notification_exists(
                db,
                current_user.id,
                reminder_24h_type,
            )
        ):
            db.add(
                Notification(
                    user_id=current_user.id,
                    title="Recordatorio de actividad",
                    message=(
                        f"Tu actividad {activity.title} "
                        "es en menos de 24 horas."
                    ),
                    notification_type=reminder_24h_type,
                )
            )

        if (
            starts_at.date() == now.date()
            and not notification_exists(
                db,
                current_user.id,
                reminder_today_type,
            )
        ):
            db.add(
                Notification(
                    user_id=current_user.id,
                    title="Recordatorio de actividad",
                    message=(
                        f"Tu actividad {activity.title} "
                        "es hoy."
                    ),
                    notification_type=reminder_today_type,
                )
            )

    db.commit()


@router.get(
    "",
    response_model=list[NotificationResponse],
)
def get_notifications(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    create_reminders(
        db,
        current_user,
    )

    return db.scalars(
        select(Notification)
        .where(Notification.user_id == current_user.id)
        .order_by(Notification.created_at.desc())
    ).all()


@router.patch(
    "/{notification_id}/leer",
    response_model=NotificationResponse,
)
def read_notification(
    notification_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    notification = db.get(Notification, notification_id)

    if notification is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Notificación no encontrada",
        )

    if notification.user_id != current_user.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="No tienes permiso para modificar esta notificación",
        )

    notification.is_read = True
    db.commit()
    db.refresh(notification)

    return notification