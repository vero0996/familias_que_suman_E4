import os
import smtplib
from email.message import EmailMessage


def send_email(to: str, subject: str, body: str) -> None:
    message = EmailMessage()
    message["From"] = os.environ["SMTP_FROM"]
    message["To"] = to
    message["Subject"] = subject
    message.set_content(body)

    with smtplib.SMTP(
        host=os.environ["SMTP_HOST"],
        port=int(os.environ["SMTP_PORT"]),
        timeout=10,
    ) as smtp:
        smtp.send_message(message)