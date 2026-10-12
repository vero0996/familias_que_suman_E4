
FROM python:3.12-slim

COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

RUN groupadd --system app \
    && useradd --system --gid app --create-home app

WORKDIR /app

RUN chown app:app /app

COPY --chown=app:app pyproject.toml uv.lock ./

USER app

RUN uv sync --locked --no-dev --no-install-project

COPY --chown=app:app . .

EXPOSE 8000

CMD ["uv", "run", "--no-sync", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
