# Monitoreo local

Agrega `GRAFANA_ADMIN_PASSWORD` a tu `.env` con una contraseña propia (no subirla a Git).

```sh
docker compose -f compose.yaml -f compose.monitoring.yaml up -d --build api prometheus grafana
```

Grafana: http://localhost:3000 (usuario `admin`, contraseña del `.env`).
Abre Dashboards → Familias que Suman → Familias que Suman — API.
Prometheus: http://localhost:9090/targets. El target familias-api debe estar UP.
Genera tráfico con la app o `curl http://localhost:8000/health` y espera 30 segundos.
Las tasas y p95 necesitan varias muestras; un panel vacío al inicio no indica un fallo.

Las métricas se sirven en api:9000 dentro de Docker, sin publicar ese puerto al host.
El túnel de la app usa api:8000, por lo que no publica el endpoint de métricas.
Grafana y Prometheus solo escuchan en loopback del host. Las métricas usan plantillas
como /activities/{activity_id}, no IDs, correos, tokens, parámetros ni cuerpos.
No hay alertas automáticas configuradas. Esta configuración asume un proceso Uvicorn;
para múltiples workers o réplicas se debe adaptar la recolección.
Los volúmenes conservan paneles y series, con retención Prometheus de siete días.
La contraseña de admin se aplica en la primera creación del volumen Grafana;
para cambiarla después, usa su interfaz de administración.

Validación:
```sh
docker compose -f compose.yaml -f compose.monitoring.yaml ps
docker compose -f compose.yaml -f compose.monitoring.yaml logs --tail=30 prometheus grafana
```
Para parar solo el monitoreo, usa `stop prometheus grafana` con los mismos archivos.
No uses `down -v`: eliminaría también los datos del proyecto.
