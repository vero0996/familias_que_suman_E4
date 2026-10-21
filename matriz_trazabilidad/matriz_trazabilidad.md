
# Matriz de Trazabilidad de Requisitos
## Familias que Suman — Reto E4

**Asignatura:** Integración de seguridad informática en redes y sistemas de software
**Grupo:** 402
**Fecha de actualización:** 21 de octubre de 2026
**Proyecto:** Familias que Suman
**Repositorio:** https://github.com/vero0996/familias_que_suman_E4

---

## 1. Objetivo

Esta matriz relaciona los requerimientos funcionales y no funcionales del proyecto con sus criterios de aceptación, las pruebas utilizadas para verificarlos, los resultados obtenidos y su estado final.

Se incluyen los requerimientos funcionales originales (RF-01 a RF-15), los funcionales propuestos (RF-16 a RF-22) y los no funcionales propuestos documentados para el proyecto.

**Importante:** un requerimiento no se considera cumplido únicamente porque esté documentado o exista una pantalla relacionada. Su cumplimiento debe demostrarse mediante pruebas y evidencia verificable.

**Validación manual:** la usuaria reporta que probó en un dispositivo físico los flujos disponibles de la aplicación y que funcionaron correctamente. Esta confirmación se registra como prueba manual reportada; al no haber capturas ni un checklist por requisito adjuntos, los estados se conservan como parciales cuando falta evidencia de algún criterio específico.

## 2. Estados utilizados

| Estado | Significado |
|---|---|
| Cumplido | El criterio de aceptación fue verificado y existe evidencia suficiente. |
| Parcialmente cumplido | Existe implementación o evidencia parcial, pero falta verificar parte del criterio. |
| Pendiente de verificación | No se dispone todavía de evidencia suficiente para determinar su cumplimiento. |
| No cumplido | La verificación demostró que el criterio no se satisface. |

## 3. Requerimientos funcionales originales

| ID | Requerimiento | Criterio de aceptación | Prueba o evidencia | Resultado | Estado final |
|---|---|---|---|---|---|
| RF-01 | Directorio de asociaciones y centros | La familia puede consultar el directorio, sus categorías y la información de una asociación o centro. | Prueba manual en dispositivo físico reportada por la usuaria; no se identificó una prueba instrumentada específica para categorías y fichas del directorio. | La usuaria reporta que los flujos disponibles funcionaron; faltan capturas o checklist que demuestren este criterio completo. | Parcialmente cumplido |
| RF-02 | Filtro por ubicación | La familia puede seleccionar o cambiar una ciudad y consultar las opciones correspondientes. | `ExplorarScreenTest` prueba búsqueda y filtros de campañas/eventos; prueba manual en dispositivo físico reportada. No se identificó prueba directa del filtro por ciudad. | Funcionaron los filtros cubiertos por las pruebas disponibles; falta demostrar selección/cambio de ubicación. | Parcialmente cumplido |
| RF-03 | Ficha de asociación | La aplicación muestra propósito, población beneficiada, necesidades, ubicación y medios de contacto de la asociación. | Prueba manual en dispositivo físico reportada por la usuaria; no se identificó una prueba instrumentada que compruebe cada campo de la ficha. | Se reporta funcionamiento de los flujos disponibles; falta evidencia que permita verificar todos los campos del criterio. | Parcialmente cumplido |
| RF-04 | Actividades de voluntariado | La familia consulta fecha, horario, ubicación, descripción, requisitos y adecuación para niños antes de inscribirse. | Prueba manual en dispositivo físico reportada; `ActivityOccupancyUiTest` verifica el renderizado de actividad, participantes y lugares disponibles; suite Android: 17/17. | Las pruebas finalizaron correctamente; no documentan todos los campos requeridos en el criterio. | Parcialmente cumplido |
| RF-05 | Disponibilidad de lugares en tiempo real | La disponibilidad se actualiza después de una inscripción o cancelación y no se excede el cupo ante solicitudes simultáneas. | `ActivityOccupancyUiTest` valida ocupación/cupos renderizados; pruebas de inscripción y cancelación de `CoreWorkflowUiTest`; suite Android: 17/17. | La interfaz y las confirmaciones se probaron; no se verificó en esta ejecución la actualización persistida ni la concurrencia de solicitudes. | Parcialmente cumplido |
| RF-06 | Inscripción con acompañantes | La inscripción registra los integrantes asistentes, incluidos nombres y edades, y permite consultar esos datos para organizar la actividad. | Prueba manual en dispositivo físico reportada; `CoreWorkflowUiTest.inscripcionPideConfirmacionAntesDeEnviarParticipantes`; suite Android: 17/17. | Se comprobó la confirmación del flujo de inscripción y la usuaria reporta funcionamiento en físico; falta evidencia de persistencia y consulta de nombres/edades en backend. | Parcialmente cumplido |
| RF-07 | Agregar actividad al calendario | La actividad aparece en el calendario del dispositivo con fecha, horario y ubicación correctos. | Pendiente: prueba en dispositivo físico o emulador y captura del evento creado. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-08 | Medios de contacto | La familia puede iniciar el medio de contacto seleccionado desde la aplicación sin copiar manualmente la información. | Pendiente: prueba de los medios de contacto disponibles. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-09 | Proyectos y campañas | La familia consulta las campañas activas desde la aplicación sin depender del sitio web. | Prueba manual en dispositivo físico: carga de campañas y consulta del enlace compartido; `ExplorarScreenTest` y suite instrumentada Android (17/17). | Se reportó carga correcta en el dispositivo físico; también se verificó previamente la respuesta de `GET /campaigns` y `GET /campaigns/3`. | Cumplido |
| RF-10 | Información para donar | La aplicación indica qué se necesita donar, dónde entregar las donaciones y los horarios disponibles; no procesa pagos ni donativos monetarios. | Pendiente: revisión de la información presentada y comprobación de que no se procesan pagos. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-11 | Perfil e historial de la familia | La familia consulta y administra sus datos personales y visualiza el historial de participación. | Prueba manual en dispositivo físico reportada; `CoreWorkflowUiTest.perfilPermiteAbrirGestionDeIntegrantesYCerrarSesion`; suite Android: 17/17. | Se comprobó navegación del perfil a gestión de integrantes y cierre de sesión; falta evidencia de actualización de datos e historial. | Parcialmente cumplido |
| RF-12 | Testimonio con fotografía | El testimonio permanece pendiente de revisión hasta que un administrador lo aprueba. | Pendiente: prueba de envío, revisión administrativa y publicación posterior a la aprobación. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-13 | Encuesta antes y después de la actividad | Las respuestas de ambas encuestas quedan almacenadas y relacionadas con la actividad correspondiente. | Pruebas de backend de encuestas; prueba manual en dispositivo físico reportada por la usuaria. | El flujo se reporta probado; falta incorporar evidencia que demuestre ambas encuestas y la relación/persistencia con la actividad. | Parcialmente cumplido |
| RF-14 | Reconocimiento por participación | La familia consulta su reconocimiento y este se actualiza según las actividades en las que participa. | Pendiente: prueba de registro de participación y actualización del reconocimiento. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-15 | Aviso de privacidad accesible | La familia consulta el aviso de privacidad y puede solicitar la eliminación de sus datos mediante el mecanismo del sistema. | Pendiente: revisión del aviso y prueba del procedimiento de eliminación de datos. | Sin resultado documentado en esta matriz. | Pendiente de verificación |

## 4. Requerimientos funcionales propuestos

Los siguientes requerimientos se incluyen porque forman parte del backlog propuesto. Su inclusión no implica que ya estén implementados.

| ID | Requerimiento | Criterio de aceptación | Prueba o evidencia | Resultado | Estado final |
|---|---|---|---|---|---|
| RF-16 | Priorización de necesidades y campañas | La asociación o el administrador activa una etiqueta de urgencia y esta se muestra destacada en la interfaz principal o en el perfil de la organización. | Pendiente: prueba de activación de urgencia y verificación de su visualización. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-17 | Recomendación de actividades | La familia visualiza recomendaciones personalizadas según sus intereses, las edades de sus integrantes y el tipo de participación disponible. | Pendiente: prueba con perfiles e intereses diferentes y comparación de recomendaciones. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-18 | Gestión simplificada de campañas | La asociación puede crear, editar y duplicar una campaña sin volver a registrar todos sus datos. | Prueba manual en dispositivo físico reportada; `CoreWorkflowUiTest` verifica formulario de creación y edición de campaña; suite Android: 17/17. | Se reporta funcionamiento correcto en físico y pasaron las pruebas de creación/edición; no se verificó duplicación ni persistencia completa. | Parcialmente cumplido |
| RF-19 | Cancelación de inscripción | La familia cancela una inscripción con al menos 72 horas de anticipación y el sistema libera el lugar y actualiza la disponibilidad. | Prueba manual en dispositivo físico reportada; `CoreWorkflowUiTest.gestionDeParticipantesPideConfirmacionParaCancelar`; suite Android: 17/17. | Se verificó la confirmación de cancelación en UI; falta evidencia del límite de 72 horas y de la liberación persistida del lugar. | Parcialmente cumplido |
| RF-20 | Lista de espera | Al cancelarse una inscripción, el sistema ofrece o asigna el lugar disponible a la primera persona o familia de la lista de espera, respetando el orden de registro. | Prueba manual en dispositivo físico reportada; `CoreWorkflowUiTest` verifica aceptación de lista de espera y disponibilidad de cupo; suite Android: 17/17. | La aceptación fue probada en UI; falta demostrar la asignación automática del primer lugar y el respeto del orden de espera. | Parcialmente cumplido |
| RF-21 | Notificaciones automatizadas | La familia recibe notificaciones push relevantes sobre inscripciones, recordatorios, lugares disponibles y campañas, y puede abrir el apartado correspondiente. | Pendiente: prueba de envío push, recepción en dispositivo y navegación al destino. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RF-22 | Comunicación automatizada con participantes | Los participantes reciben un mensaje de confirmación con información pertinente y, cuando corresponda, una liga para contactar a la asociación y a otros participantes. | Pendiente: prueba de envío, recepción y contenido del mensaje de confirmación. | Sin resultado documentado en esta matriz. | Pendiente de verificación |

## 5. Requerimientos no funcionales propuestos

Se conservan los identificadores documentados en el archivo de requerimientos. El segundo aparece como `RFN02` en la fuente original; debe confirmarse si el identificador oficial del backlog es ese o `RNF-02`.

| ID | Requerimiento | Criterio de aceptación | Prueba o evidencia | Resultado | Estado final |
|---|---|---|---|---|---|
| RNF-01 | Seguridad en tránsito | La comunicación entre la aplicación y la API utiliza HTTPS con TLS 1.2 o superior en todos los endpoints. | Pendiente: comprobación de HTTPS/TLS y revisión de la configuración de los endpoints. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RFN02 | Privacidad de datos | Las contraseñas se almacenan mediante hashing seguro y nunca en texto plano; los datos personales solo son accesibles por funciones autorizadas. | Pendiente: revisión del mecanismo de hashing y pruebas de acceso autorizado y no autorizado. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RNF-03 | Rendimiento de multimedia | Las fotografías se comprimen antes de enviarse al servidor y tienen un tamaño máximo de 2 MB, conservando calidad suficiente. | Pendiente: prueba con fotografías de diferentes tamaños y comprobación del archivo enviado. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| RNF-04 | Disponibilidad | El sistema mantiene un uptime mensual mínimo del 99.99 %, excluyendo mantenimientos programados. | Pendiente: registros de monitoreo y cálculo de disponibilidad durante un periodo mensual. | No hay registros de disponibilidad mensual documentados en esta matriz. | Pendiente de verificación |

## 6. Requisitos técnicos y de seguridad de la entrega final

Además de los requisitos anteriores, la entrega solicita evidencia de controles de seguridad en la API y de despliegue seguro. Estos puntos deben vincularse con los IDs del backlog cuando existan; no se deben inventar identificadores para reemplazar los originales.

| Control solicitado | Criterio de verificación | Prueba o evidencia | Resultado | Estado final |
|---|---|---|---|---|
| Autenticación y manejo de sesiones | Las credenciales y los tokens se validan de forma segura, y los tokens inválidos o expirados no permiten acceder a recursos protegidos. | Pendiente: pruebas con credenciales inválidas y tokens manipulados o expirados. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| Autorización por rol y propiedad del recurso | Un usuario no puede consultar ni modificar recursos que no le corresponden. | Pendiente: pruebas con usuarios diferentes y accesos a recursos ajenos. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| Validación de entradas e inyección SQL | Las entradas se validan y las consultas se construyen de manera segura. | Pendiente: pruebas negativas y revisión de consultas parametrizadas u ORM. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| Límites de peticiones | Los endpoints críticos cuentan con límites de tasa, control de concurrencia o bloqueo temporal contra abusos. | Pendiente: pruebas de exceso de peticiones en endpoints críticos. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| Cabeceras de seguridad HTTP | Se configuran las cabeceras de seguridad pertinentes para el servicio. | Pendiente: revisión de las respuestas HTTP de la API. | Sin resultado documentado en esta matriz. | Pendiente de verificación |
| Contenedores y gestión de secretos | La API se despliega en contenedores con configuración segura y secretos administrados mediante variables de entorno u otro mecanismo apropiado. | Pendiente: inspección de la configuración de despliegue y de la gestión de secretos. | Se ha trabajado con Docker; falta documentar la verificación de los controles solicitados. | Parcialmente cumplido |
| Acceso externo seguro a la API | El túnel permite la demostración desde la aplicación y el servicio utiliza los protocolos de protección definidos. | Pendiente: prueba de acceso externo y revisión de HTTPS/TLS. | Se han presentado incidencias de conexión del túnel; no se considera verificado el acceso externo estable. | Pendiente de verificación |
| Pruebas de seguridad | La API rechaza solicitudes maliciosas o no autorizadas. | Pendiente: evidencia de intentos de inyección, tokens inválidos, accesos ajenos y exceso de peticiones. | Sin resultados completos documentados en esta matriz. | Pendiente de verificación |
| Análisis dinámico con OWASP ZAP | Se ejecuta un escaneo DAST y se documentan los hallazgos relevantes y sus mitigaciones. | Pendiente: reporte exportado de OWASP ZAP y registro de las mitigaciones. | No se ha incorporado un reporte de escaneo a esta matriz. | Pendiente de verificación |

## 7. Relación con historias de usuario y casos de uso

La relación entre los requisitos y las historias de usuario debe conservar los identificadores de los documentos originales. No se asignan relaciones definitivas cuando no se dispone del contenido completo para verificarlas.

| Requisito | Historia de usuario relacionada | Caso de uso relacionado | Evidencia |
|---|---|---|---|
| RF-01 a RF-04 | Por vincular con las HU documentadas | Explorar y filtrar el directorio de ayuda | Pendiente de verificar |
| RF-05 y RF-06 | Por vincular con las HU documentadas | Agendar o inscribirse a una actividad | Pendiente de verificar |
| RF-18 | Por vincular con las HU documentadas | Crear o reutilizar una campaña (vista de administrador) | Pendiente de verificar |
| RF-19 y RF-20 | Por vincular con las HU documentadas | Flujo de inscripción y cancelación | Pendiente de verificar |
| RF-22 | Por vincular con las HU documentadas | Comunicación con participantes | Pendiente de verificar |

## 8. Evidencias por recopilar

Utiliza estos nombres como convención para organizar las evidencias. No representan archivos existentes hasta que se incorporen al repositorio o a la entrega.

### Resultados de pruebas Android

| Ejecución | Resultado | Alcance y límite |
|---|---|---|
| `.\gradlew.bat :app:connectedDebugAndroidTest` en `Pixel_10` | 17 pruebas aprobadas, 0 fallidas (resultado reportado por la usuaria). | Pruebas instrumentadas de UI, navegación, validaciones y callbacks; por sí solas no demuestran persistencia ni integración de todos los flujos con backend. |
| `.\gradlew.bat :app:testDebugUnitTest` | 23 pruebas aprobadas, 0 fallidas. | Pruebas unitarias Android; no sustituyen pruebas manuales de todos los criterios funcionales. |
| Prueba manual en dispositivo físico | La usuaria reporta que probó los flujos disponibles de la app y que funcionaron correctamente. | Registrar capturas o checklist por requisito para hacer trazable qué criterios se validaron en físico. |

| ID de evidencia sugerido | Evidencia necesaria | Requisitos relacionados |
|---|---|---|
| EV-01 | Capturas de navegación del directorio, filtros y fichas; la prueba manual en físico fue reportada, pero faltan capturas por requisito. | RF-01, RF-02, RF-03 |
| EV-02 | Pruebas instrumentadas y validación manual en físico de actividades y campañas; conservar capturas de los recorridos. | RF-04, RF-09 |
| EV-03 | Pruebas de inscripción y ocupación ejecutadas; faltan evidencia de persistencia, acompañantes y concurrencia. | RF-05, RF-06 |
| EV-04 | Prueba de integración con el calendario del dispositivo | RF-07 |
| EV-05 | Prueba instrumentada de navegación de perfil ejecutada; falta evidencia de actualización e historial. | RF-11 |
| EV-06 | Pruebas de envío, revisión y aprobación de testimonios | RF-12 |
| EV-07 | Pruebas de encuestas disponibles; falta evidencia del vínculo y persistencia de encuestas antes y después por actividad. | RF-13 |
| EV-08 | Pruebas instrumentadas de confirmación para cancelar y aceptar lista de espera ejecutadas; falta evidencia de reglas de plazo, liberación y orden. | RF-19, RF-20 |
| EV-09 | Pruebas instrumentadas de creación y edición ejecutadas; falta evidencia de duplicación y persistencia completa. | RF-18 |
| EV-10 | Pruebas de autenticación, autorización y validación de entradas | Controles de seguridad |
| EV-11 | Evidencia de HTTPS/TLS, cabeceras y despliegue | RNF-01 y controles de seguridad |
| EV-12 | Pruebas de compresión de imágenes | RNF-03 |
| EV-13 | Registros de disponibilidad | RNF-04 |
| EV-14 | Reporte DAST de OWASP ZAP | Pruebas de seguridad |
| EV-15 | Pruebas de notificaciones y comunicación automatizada | RF-21, RF-22 |
| EV-16 | Evidencia de privacidad, eliminación de datos y reconocimiento | RF-10, RF-14, RF-15 y privacidad |

## 9. Criterios de cierre

Antes de marcar un requisito como cumplido, se debe:

1. Identificar el requisito con el mismo ID del backlog.
2. Comprobar que existe una implementación relacionada.
3. Ejecutar una prueba que cubra su criterio de aceptación.
4. Guardar el resultado y la evidencia reproducible.
5. Registrar cualquier limitación o condición que impida considerarlo completamente cumplido.
6. Actualizar el estado de la matriz con base en el resultado real.

La matriz debe actualizarse conforme se completen y verifiquen las funcionalidades. Los requisitos propuestos también se conservan en el documento, independientemente de que su implementación se haya realizado o no.

## 10. Referencias del proyecto

- Repositorio: https://github.com/vero0996/familias_que_suman_E4
- Documento de requerimientos funcionales y no funcionales.
- Documento de prototipos, requerimientos validados y backlog.
- Resultados de pruebas unitarias y de integración.
- Reportes de pruebas de seguridad y despliegue.

**Nota final:** esta matriz constituye una versión inicial de trazabilidad. Los estados y las evidencias deben actualizarse después de revisar los tests, el código, los resultados de ejecución y la configuración real del sistema.
