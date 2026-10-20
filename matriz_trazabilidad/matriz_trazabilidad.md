
# Matriz de trazabilidad de historias de usuario
## Proyecto: Familias que Suman+

### 1. Objetivo

Relacionar las historias de usuario (HU) con las suites de pruebas automatizadas del proyecto, identificando la evidencia disponible y el nivel de cobertura de cada historia.

### 2. Criterios de evaluación

- **Validación directa:** las pruebas verifican específicamente el comportamiento descrito en la historia de usuario.
- **Cobertura parcial:** existen pruebas relacionadas, pero no verifican todos los escenarios o el flujo completo.
- **Pendiente de evidencia:** no se ha identificado una prueba que compruebe directamente la funcionalidad solicitada.

Que una suite de pruebas pase significa que los casos incluidos se ejecutaron correctamente; no implica que toda la historia de usuario esté validada.

### 3. Matriz de trazabilidad

| ID | Historia de usuario | Suites de pruebas relacionadas | Evidencia disponible | Estado |
|---|---|---|---|---|
| HU-01 | **Filtros.** Como usuario de la plataforma, quiero filtrar causas y campañas por tipo de ayuda, ubicación o asociación, para encontrar una actividad adecuada para mi familia. | `test_campaign_activity_security.py`, `test_projects_security.py`, `test_events_security.py` | Las pruebas se relacionan con campañas, actividades, proyectos y eventos. No se ha confirmado que comprueben específicamente los filtros por tipo de ayuda, ubicación y asociación. | Cobertura parcial |
| HU-02 | **Directorio.** Como padre de familia, quiero acceder a un directorio actualizado de asilos, comedores y asociaciones, para saber a dónde acudir a realizar una visita de apoyo. | Sin suite directa identificada | Las pruebas de proyectos y eventos no demuestran por sí solas que el directorio de asociaciones y centros funcione y esté actualizado. | Pendiente de evidencia |
| HU-03 | **Comprobación social.** Como voluntario, quiero subir una fotografía y una breve descripción de la actividad realizada, para inspirar a otras familias a sumarse. | `test_photos_security.py` | Se prueban la autenticación para subir fotografías, los formatos JPG, PNG y WEBP permitidos en los casos probados y el rechazo de archivos TXT y PDF. No se ha confirmado la cobertura de la descripción, publicación y revisión del testimonio. | Cobertura parcial |
| HU-04 | **Registro seguro.** Como nuevo usuario, quiero crear una cuenta con nombre, apellido, teléfono y correo electrónico de forma segura, para guardar mis preferencias y participar en proyectos. | `test_auth_registration_security.py`, `test_auth_login_security.py`, `test_security.py`, `test_rate_limit_security.py` | Las suites están relacionadas con autenticación, registro y seguridad. Se debe confirmar que las pruebas cubran todos los campos y las validaciones requeridas por la historia. | Cobertura parcial |
| HU-05 | **Calendario.** Como usuario, quiero agregar una actividad en la que estoy inscrito al calendario de mi dispositivo para recordar la fecha, el horario y la ubicación. | Sin suite directa identificada | No se ha presentado evidencia de pruebas de integración con el calendario del dispositivo ni de la transferencia correcta de los datos de la actividad. | Pendiente de evidencia |
| HU-06 | **Notificaciones.** Como padre de familia, quiero recibir avisos automáticos y recordatorios sobre mis actividades, para conocer confirmaciones, cambios en mis inscripciones y disponibilidad de lugares. | Sin suite directa identificada | Las pruebas de seguridad de actividades e inscripciones no demuestran por sí solas el envío de notificaciones y recordatorios automáticos. | Pendiente de evidencia |
| HU-07 | **Lista de espera y cancelación.** Como participante, quiero entrar en una lista de espera cuando un evento esté lleno y cancelar mi inscripción con anticipación para liberar mi lugar. | `test_registration_validation.py`, `test_registration_ownership.py`, `test_campaign_activity_security.py` | Las suites se relacionan con validaciones de inscripciones, propiedad y actividades. Se requiere comprobar específicamente el cupo lleno, la lista de espera, la cancelación y la reasignación de lugares. | Cobertura parcial |
| HU-08 | **Mensajes grupales y privados.** Como participante, quiero comunicarme mediante mensajes grupales o privados con asociaciones, administradores y otros participantes. | `test_messages_security.py` | Se prueban la autenticación, el envío y consulta de mensajes privados, la identificación del remitente, las validaciones de entrada, el aislamiento de conversaciones y la persistencia. No se ha demostrado la funcionalidad de mensajes grupales ni la comunicación con todos los roles indicados. | Cobertura parcial |
| HU-09 | **Compartir campañas.** Como usuario, quiero compartir campañas con otras personas para darles mayor difusión e invitar a más familias. | Sin suite directa identificada | No se ha presentado evidencia de pruebas de la funcionalidad para compartir campañas o generar y abrir enlaces de difusión. | Pendiente de evidencia |
| HU-10 | **Reconocimiento de participación.** Como padre de familia, quiero recibir insignias o niveles por las actividades en las que participe para visualizar la trayectoria de servicio de mi familia. | Sin suite directa identificada | No se ha presentado evidencia de pruebas para el cálculo de participación, la asignación de insignias o niveles y la actualización del reconocimiento de la familia. | Pendiente de evidencia |

### 4. Observaciones

- **HU-01:** comprobar que los filtros por tipo de ayuda, ubicación y asociación tengan pruebas específicas.
- **HU-02:** verificar el directorio de asociaciones y centros, así como la información mostrada en sus fichas.
- **HU-03:** verificar la descripción, publicación y revisión de los testimonios con fotografía.
- **HU-04:** confirmar la validación de todos los campos del registro y los casos positivos y negativos.
- **HU-05:** verificar la integración con el calendario del dispositivo.
- **HU-06:** comprobar el envío de notificaciones en los escenarios definidos.
- **HU-07:** probar el flujo completo de lista de espera, cancelación y reasignación de cupos.
- **HU-08:** verificar los mensajes grupales y los permisos de comunicación de los diferentes roles.
- **HU-09:** probar la funcionalidad para compartir campañas.
- **HU-10:** verificar las reglas para asignar y actualizar insignias o niveles de reconocimiento.

### 5. Resultado de la ejecución automatizada

Según el resultado reportado por el equipo, todas las pruebas automatizadas ejecutadas finalizaron correctamente.

| Métrica | Resultado |
|---|---|
| Comando de ejecución | `uv run pytest` (confirmar con la ejecución final) |
| Pruebas aprobadas | Completar con el resumen de la terminal |
| Pruebas fallidas | 0, según el resultado reportado |
| Fecha de ejecución | Completar con la fecha de la ejecución documentada |

### 6. Conclusión

Las pruebas automatizadas ejecutadas pasaron correctamente, de acuerdo con el resultado reportado. Sin embargo, la aprobación de las pruebas no implica que todas las historias de usuario estén completamente validadas.

La matriz permite identificar qué funcionalidades cuentan con evidencia relacionada y cuáles requieren pruebas específicas adicionales o documentación de pruebas existentes. Los estados deberán actualizarse cuando se confirme la cobertura directa de cada historia.

**Nota:** antes de entregar, verificar los nombres de las suites contra los archivos reales del repositorio y completar las métricas con el resultado exacto de la ejecución final.
