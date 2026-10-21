import os

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from sqlalchemy import create_engine, text
from sqlalchemy.exc import SQLAlchemyError
from admin import router as admin_router
from auth import router as auth_router
from campaigns import router as campaigns_router
from activities import router as activities_router
from registrations import router as registrations_router
from family_members import router as family_members_router
from slowapi import _rate_limit_exceeded_handler
from slowapi.errors import RateLimitExceeded
from rate_limiter import limiter
from surveys import router as surveys_router
from projects import router as projects_router
from events import router as events_router
from workshops import router as workshops_router
from photos import UPLOADS_DIR, router as photos_router
from donations import router as donations_router
from messages import router as messages_router
from notifications import router as notifications_router
from password_recovery import router as password_recovery_router


app = FastAPI(
    title="Familias que Suman API",
    version="0.2.0",
    description="Backend de Familias que Suman+",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://familiasquesuman.com"],
    allow_credentials=True,
    allow_methods=["GET", "POST", "PUT", "PATCH", "DELETE"],
    allow_headers=["Authorization", "Content-Type"],
)

app.state.limiter = limiter
app.add_exception_handler(
    RateLimitExceeded,
    _rate_limit_exceeded_handler,
)

database_url = os.getenv("DATABASE_URL")
engine = (
    create_engine(database_url, pool_pre_ping=True)
    if database_url
    else None
)

app.include_router(auth_router)
app.include_router(campaigns_router)
app.include_router(admin_router)
app.include_router(activities_router)
app.include_router(registrations_router)
app.include_router(family_members_router)
app.include_router(surveys_router)
app.include_router(projects_router)
app.include_router(events_router)
app.include_router(workshops_router)
app.include_router(photos_router)
app.include_router(donations_router)
app.include_router(messages_router)
app.include_router(notifications_router)
app.include_router(password_recovery_router)
app.mount(
    "/uploads",
    StaticFiles(directory=UPLOADS_DIR),
    name="uploads",
)


@app.get("/")
def root():
    return {
        "message": "Familias que Suman API",
        "docs": "/docs",
    }


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/health/db")
def database_health():
    if engine is None:
        raise HTTPException(
            status_code=503,
            detail="Database is not configured",
        )

    try:
        with engine.connect() as connection:
            connection.execute(text("SELECT 1"))

        return {"status": "ok", "database": "connected"}

    except SQLAlchemyError:
        raise HTTPException(
            status_code=503,
            detail="Database connection failed",
        )