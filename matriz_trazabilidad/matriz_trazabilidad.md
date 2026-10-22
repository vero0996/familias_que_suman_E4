# Matriz de Trazabilidad de Requerimientos Funcionales

## Familias que Suman — Reto E4

**Curso:** Integración de seguridad informática en redes y sistemas de software

**Grupo:** 402

**Fecha de actualización:** 21 de octubre de 2026

## 1. Estados utilizados

| Estado | Descripción |
|---|---|
| Cumplido | Existe evidencia suficiente de que el criterio de aceptación se satisface. |
| Parcialmente cumplido | Se implementó o verificó una parte del requisito, pero falta comprobar el criterio completo. |
| Pendiente de verificación | No existe evidencia suficiente para confirmar el cumplimiento. |
| No cumplido | Se comprobó que el requisito no se satisface. |


## 2. Requerimientos funcionales originales

| ID | Requerimiento | Criterio de aceptación | Pruebas o evidencias disponibles | Resultado y limitaciones | Estado |
|---|---|---|---|---|---|
| RF-01 | Directorio de asociaciones y centros | La familia puede consultar el directorio, sus categorías y la información de una asociación o centro. | Validación manual reportada por la usuaria. | La usuaria confirma que verificó manualmente el criterio completo; no se adjuntó checklist ni evidencia visual. | Cumplido |
| RF-02 | Filtro por ubicación | La familia puede seleccionar o cambiar una ciudad y consultar las opciones correspondientes. | `ExplorarScreenTest`; validación manual reportada por la usuaria. | La usuaria indica que este requerimiento no se cumplió. | No cumplido |
| RF-03 | Ficha de asociación | La aplicación muestra propósito, población beneficiada, necesidades, ubicación y medios de contacto. | Validación manual reportada por la usuaria. | La usuaria confirma que verificó manualmente el criterio completo; no se adjuntó checklist ni evidencia visual. | Cumplido |
| RF-04 | Actividades de voluntariado | La familia consulta fecha, horario, ubicación, descripción, requisitos y adecuación para niños antes de inscribirse. | Validación manual reportada por la usuaria; `RequisitosFuncionalesUiTest.detalleDeActividadMuestraDescripcionFechaLugarYOcupacion`; `ActivityOccupancyUiTest`. | La usuaria confirma la verificación manual del criterio completo. Las pruebas automatizadas comprueban solo parte de los campos. | Cumplido |
| RF-05 | Disponibilidad de lugares en tiempo real | La disponibilidad se actualiza después de una inscripción o cancelación y no se excede el cupo ante solicitudes simultáneas. | Validación manual reportada por la usuaria; `RequisitosFuncionalesUiTest.detalleDeActividadMuestraDescripcionFechaLugarYOcupacion`; `ActivityOccupancyUiTest`; pruebas backend de ocupación y concurrencia. | La usuaria confirma la verificación manual del criterio completo. Las pruebas automatizadas comprueban la ocupación mostrada y reglas de backend. | Cumplido |
| RF-06 | Inscripción con acompañantes | La inscripción registra a los integrantes asistentes, incluidos nombres y edades, y permite consultar esos datos para organizar la actividad. | Validación manual reportada por la usuaria; `CoreWorkflowUiTest.inscripcionPideConfirmacionAntesDeEnviarParticipantes`; `test_registration_ownership.py`; `test_registration_validation.py`. | La usuaria confirma la verificación manual del criterio completo; las pruebas automatizadas cubren confirmación, validación y propiedad de datos. | Cumplido |
| RF-07 | Agregar actividad al calendario | La actividad aparece en el calendario del dispositivo con fecha, horario y ubicación correctos. | Validación manual reportada por la usuaria; `RequisitosFuncionalesUiTest.calendarioMuestraActividadEnSuFechaYLlamaAlSeleccionarla`; `RequisitosFuncionalesUiTest.inscripcionConfirmadaPermiteAbrirCalendarioDeActividades`. | La usuaria confirma manualmente que la actividad aparece en el calendario con los datos requeridos; las pruebas automatizadas cubren el calendario interno y su navegación. | Cumplido |
| RF-08 | Medios de contacto | La familia puede iniciar el medio de contacto seleccionado desde la aplicación sin copiar manualmente la información. | Validación manual reportada por la usuaria. | La usuaria confirma que verificó manualmente el criterio; no se adjuntó checklist ni evidencia visual. | Cumplido |
| RF-09 | Proyectos y campañas | La familia consulta las campañas activas desde la aplicación sin depender del sitio web. | `ExplorarScreenTest`; prueba manual reportada; comprobaciones previas de `GET /campaigns` y `GET /campaigns/3`. | Se reportó la carga correcta de campañas en el dispositivo y existen comprobaciones del endpoint. | Cumplido |
| RF-10 | Información para donar | La aplicación indica qué se necesita donar, dónde entregar las donaciones y los horarios disponibles. No procesa pagos ni donativos monetarios. | Validación manual reportada por la usuaria. | La usuaria confirma que verificó manualmente el criterio completo; no se adjuntó checklist ni evidencia visual. | Cumplido |
| RF-11 | Perfil e historial de la familia | La familia consulta y administra sus datos personales y visualiza el historial de participación. | Validación manual reportada por la usuaria; `CoreWorkflowUiTest.perfilPermiteAbrirGestionDeIntegrantesYCerrarSesion`; `test_family_members_security.py`. | La usuaria confirma la verificación manual del criterio completo; las pruebas automatizadas cubren navegación, integrantes y protección de acceso. | Cumplido |
| RF-12 | Testimonio con fotografía | El testimonio permanece pendiente de revisión hasta que un administrador lo aprueba. | Validación manual reportada por la usuaria. | La usuaria indica que este requerimiento no se cumplió. | No cumplido |
| RF-13 | Encuesta antes y después de la actividad | Las respuestas de ambas encuestas quedan almacenadas y relacionadas con la actividad correspondiente. | Validación manual reportada por la usuaria; pruebas de backend en `test_survey_security.py`. | La usuaria confirma la verificación manual de ambas encuestas; las pruebas automatizadas cubren permisos, creación, duplicados y validación de calificaciones. | Cumplido |
| RF-14 | Reconocimiento por participación | La familia consulta su reconocimiento y este se actualiza según las actividades en las que participa. | Validación manual reportada por la usuaria. | La usuaria indica que este requerimiento no se cumplió. | No cumplido |
| RF-15 | Aviso de privacidad accesible | La familia consulta el aviso de privacidad y puede solicitar la eliminación de sus datos mediante el mecanismo del sistema. | Aviso incluido en la documentación del proyecto; `test_auth_login_security.py`, `test_auth_registration_security.py` y pruebas de autorización. | El aviso está en el documento, pero no se integró en la aplicación. Tampoco se documentó la verificación del procedimiento completo de eliminación de datos. | Parcialmente cumplido |

## 3. Requerimientos funcionales propuestos

Los siguientes requerimientos se conservan como propuestos para distinguirlos de los requerimientos originales. Su incorporación al alcance definitivo debe contrastarse con el backlog y el acta de acuerdos del proyecto.

| ID | Requerimiento | Criterio de aceptación | Pruebas o evidencias disponibles | Resultado y limitaciones | Estado |
|---|---|---|---|---|---|
| RF-16 | Priorización de necesidades y campañas | La asociación o el administrador activa una etiqueta de urgencia y esta se muestra destacada en la interfaz. | Validación manual reportada por la usuaria. | La usuaria confirma que verificó manualmente el criterio completo; no se adjuntó checklist ni evidencia visual. | Cumplido |
| RF-17 | Recomendación de actividades | La familia visualiza recomendaciones personalizadas según intereses, edades de sus integrantes y tipo de participación disponible. | Validación manual reportada por la usuaria. | La usuaria indica que este requerimiento no se cumplió. | No cumplido |
| RF-18 | Gestión simplificada de campañas | El usuario autorizado puede crear, editar y duplicar una campaña sin volver a registrar todos sus datos. | Validación manual reportada por la usuaria; `CoreWorkflowUiTest`; `test_campaign_activity_security.py`. | La usuaria confirma que verificó manualmente la creación, edición y duplicación; las pruebas automatizadas cubren creación, edición y permisos. | Cumplido |
| RF-19 | Cancelación de inscripción | La familia cancela una inscripción con al menos 72 horas de anticipación y el sistema libera el lugar y actualiza la disponibilidad. | Validación manual reportada por la usuaria; `CoreWorkflowUiTest.gestionDeParticipantesPideConfirmacionParaCancelar`; pruebas backend de cancelación. | La usuaria confirma que verificó manualmente la regla de 72 horas y la actualización del lugar; las pruebas automatizadas cubren cancelación y lista de espera. | Cumplido |
| RF-20 | Lista de espera | Al cancelarse una inscripción, el sistema ofrece o asigna el lugar disponible a la primera familia de la lista de espera, respetando el orden de registro y la capacidad. | Validación manual reportada por la usuaria; `test_registration_ownership.py`; `test_registration_concurrency.py`; `CoreWorkflowUiTest`. | La usuaria confirma que verificó manualmente el flujo completo; las pruebas automatizadas cubren capacidad, orden de espera, concurrencia y promoción. | Cumplido |
| RF-21 | Notificaciones automatizadas | La familia recibe notificaciones push relevantes sobre inscripciones, recordatorios, lugares disponibles y campañas, y puede abrir el apartado correspondiente. | Validación manual reportada por la usuaria; `test_notifications_security.py`. | La usuaria confirma que verificó manualmente la recepción y navegación de las notificaciones; la prueba automatizada cubre acceso y lectura. | Cumplido |
| RF-22 | Comunicación automatizada con participantes | Los participantes reciben un mensaje de confirmación con información pertinente y, cuando corresponda, un enlace para contactar a la asociación y otros participantes. | Validación manual reportada por la usuaria. | La usuaria confirma que verificó manualmente el contenido y recepción de las comunicaciones; no se adjuntó checklist ni evidencia visual. | Cumplido |

## 4. Relación de requerimientos funcionales con historias de usuario y casos de uso

Los identificadores de historias de usuario deben completarse con los del backlog oficial. La siguiente tabla establece una relación funcional provisional sin inventar identificadores.

| Requisitos relacionados | Historia de usuario o flujo funcional | Casos de prueba relacionados | Estado de trazabilidad |
|---|---|---|---|
| RF-01 a RF-04 | Explorar directorio, buscar asociaciones, consultar fichas y explorar actividades. | `ExplorarScreenTest`; `RequisitosFuncionalesUiTest`; `ActivityOccupancyUiTest`; validación manual reportada. | RF-01, RF-03 y RF-04 cumplidos manualmente; RF-02 no cumplido. Falta relacionar con HU oficiales. |
| RF-05 y RF-06 | Consultar ocupación e inscribirse con integrantes de la familia. | `CoreWorkflowUiTest`; `RequisitosFuncionalesUiTest`; `test_activity_occupancy.py`; `test_registration_ownership.py`; `test_registration_validation.py`; validación manual reportada. | Cumplidos según validación manual reportada. |
| RF-07 y RF-08 | Agregar una actividad al calendario y abrir un medio de contacto. | `RequisitosFuncionalesUiTest`; validación manual reportada. | Cumplidos según validación manual reportada. |
| RF-09 y RF-10 | Consultar campañas e información para donar. | `ExplorarScreenTest`; comprobaciones previas de endpoints de campañas; validación manual reportada. | Cumplidos según evidencia automatizada y validación manual reportada, respectivamente. |
| RF-11 y RF-15 | Administrar perfil e integrantes y proteger los datos personales. | `CoreWorkflowUiTest`; `test_family_members_security.py`; pruebas de autenticación y autorización; aviso de privacidad en la documentación. | RF-11 cumplido según validación manual; RF-15 parcialmente cumplido: aviso documentado, no integrado en la app y eliminación de datos pendiente de verificar. |
| RF-12 | Enviar testimonio, revisar y aprobar publicación. | Validación manual reportada. | No cumplido según validación manual reportada. |
| RF-13 | Crear y consultar encuestas relacionadas con la participación. | `test_survey_security.py`; validación manual reportada. | Cumplido según validación manual reportada. |
| RF-14 | Consultar y actualizar el reconocimiento por participación. | Validación manual reportada. | No cumplido según validación manual reportada. |
| RF-16 y RF-17 | Priorizar campañas y recomendar actividades. | Validación manual reportada. | RF-16 cumplido; RF-17 no cumplido según validación manual reportada. |
| RF-18 | Crear, editar y duplicar campañas. | `CoreWorkflowUiTest`; `test_campaign_activity_security.py`; validación manual reportada. | Cumplido según validación manual reportada. |
| RF-19 y RF-20 | Cancelar inscripciones y gestionar lista de espera. | `test_registration_ownership.py`; `test_registration_concurrency.py`; `test_campaign_activity_security.py`; validación manual reportada. | Cumplidos según validación manual reportada. |
| RF-21 | Consultar y recibir notificaciones. | `test_notifications_security.py`; validación manual reportada. | Cumplido según validación manual reportada. |
| RF-22 | Enviar comunicaciones automáticas a participantes. | Validación manual reportada. | Cumplido según validación manual reportada. |