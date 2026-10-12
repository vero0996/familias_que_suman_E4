
import os

from fastapi import FastAPI, HTTPException
from sqlalchemy import create_engine, text
from sqlalchemy.exc import SQLAlchemyError

app = FastAPI(
    title="Familias que Suman API",
    version="0.1.0",
    description="Backend de Familias que Suman+",
)

database_url = os.getenv("DATABASE_URL")
engine = (
    create_engine(database_url, pool_pre_ping=True)
    if database_url
    else None
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
