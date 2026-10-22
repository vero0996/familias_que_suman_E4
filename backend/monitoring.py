"""HTTP metrics without tokens, request bodies or concrete resource IDs."""
import os
import time
from contextlib import asynccontextmanager
from prometheus_client import Counter, Histogram, start_http_server
from starlette.middleware.base import BaseHTTPMiddleware

REQUESTS = Counter("familias_http_requests_total", "Completed HTTP requests", ["method", "route", "status"])
DURATION = Histogram("familias_http_request_duration_seconds", "Request duration", ["method", "route"])

@asynccontextmanager
async def metrics_lifespan(app):
    server = thread = None
    if os.getenv("METRICS_ENABLED", "false").lower() == "true":
        server, thread = start_http_server(int(os.getenv("METRICS_PORT", "9000")), addr="0.0.0.0")
    try:
        yield
    finally:
        if server:
            server.shutdown()
            server.server_close()
            thread.join(timeout=5)

class MetricsMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request, call_next):
        started = time.perf_counter()
        status = 500
        try:
            response = await call_next(request)
            status = response.status_code
            return response
        finally:
            route = getattr(request.scope.get("route"), "path", "unmatched")
            method = request.method if request.method in {"GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS"} else "OTHER"
            REQUESTS.labels(method, route, str(status)).inc()
            DURATION.labels(method, route).observe(time.perf_counter() - started)
