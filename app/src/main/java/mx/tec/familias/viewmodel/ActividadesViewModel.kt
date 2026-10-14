
package mx.tec.familias.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.tec.familias.data.model.Activity
import mx.tec.familias.data.model.EstadoActividades
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.repository.ActividadesRepository
import mx.tec.familias.data.repository.ActivityRemoteRepository
import mx.tec.familias.data.repository.CampaignRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first
import mx.tec.familias.data.remote.RegistrationRequest
import mx.tec.familias.data.repository.RegistrationRemoteRepository
import mx.tec.familias.data.remote.SurveyCreateRequest
import mx.tec.familias.data.remote.SurveyResponse
import mx.tec.familias.data.repository.SurveyRemoteRepository

class ActividadesViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val repositorioRemoto = ActivityRemoteRepository()
    private val campaignRepository = CampaignRepository()

    private val registrationRepository = RegistrationRemoteRepository()

    var ultimaInscripcionEstado by mutableStateOf<String?>(null)
        private set
    private var repositorio = ActividadesRepository.prototipo()

    var estado by mutableStateOf(repositorio.consultar())
        private set

    var eventoSeleccionadoId by mutableStateOf("")

    var ultimaInscripcionId by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private val surveyRepository = SurveyRemoteRepository()

    var encuestasEnviadas by mutableStateOf<List<SurveyResponse>>(emptyList())
        private set

    var cargandoEncuestas by mutableStateOf(false)
        private set

    var errorEncuestas by mutableStateOf<String?>(null)
        private set

    init {
        cargarActividades()
    }

    fun cargarActividades() {
        viewModelScope.launch {
            cargando = true
            error = null

            try {
                // Las campañas aportan el nombre de la organización.
                val campanias = runCatching {
                    campaignRepository.getCampaigns()
                }.getOrDefault(emptyList())

                val actividadesRemotas = repositorioRemoto.getActivities()

                val organizaciones = campanias.associate {
                    it.id to it.organization_name
                }

                val actividades = actividadesRemotas.map { remote ->
                    val fechaInicio = parseDateTime(remote.starts_at)

                    Activity(
                        id = remote.id.toString(),
                        nombre = remote.title,
                        organizacion = remote.campaign_id
                            ?.let { organizaciones[it] }
                            ?: "Organización",
                        descripcion = remote.description,
                        fecha = fechaInicio?.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) ?: "",
                        hora = fechaInicio?.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ) ?: "",
                        lugar = remote.location,
                        capacidadPersonas = remote.capacity_people,
                        cierreInscripciones = parseEpochMillis(
                            remote.registration_deadline
                        ),
                        ocupacionInicial = 0,
                        url = "",
                        puntoEncuentro = remote.location,
                        tipoAportacion = "",
                        requisitos = "",
                        contacto = "",
                        endsAtEpochMillis = remote.ends_at?.let {
                            parseEpochMillis(it)
                        }
                    )
                }

                // Se reemplazan los datos de prueba por las actividades reales.
                repositorio = ActividadesRepository(actividades)
                estado = repositorio.consultar()

                if (actividades.none { it.id == eventoSeleccionadoId }) {
                    eventoSeleccionadoId = actividades.firstOrNull()?.id.orEmpty()
                }
            } catch (e: Exception) {
                error = e.message
                    ?: "No se pudieron cargar las actividades del servidor."
            } finally {
                cargando = false
            }
        }
    }

    fun cargarMisInscripcionesYEncuestas(familiaId: String) {
        viewModelScope.launch {
            cargandoEncuestas = true
            errorEncuestas = null

            try {
                val token = sessionManager.token.first()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Inicia sesión para consultar tus encuestas."
                    )

                // 1. Cargar las inscripciones reales de la familia.
                val inscripcionesRemotas =
                    registrationRepository.getMyRegistrations(token)

                val inscripcionesLocales = inscripcionesRemotas.mapNotNull { remote ->
                    val estadoRemoto = when (remote.status.uppercase()) {
                        "CONFIRMADA" ->
                            mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA

                        "EN_ESPERA", "LISTA_ESPERA" ->
                            mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA

                        else -> null
                    }

                    estadoRemoto?.let {
                        mx.tec.familias.data.model.Inscripcion(
                            id = remote.id.toString(),
                            eventoId = remote.activity_id.toString(),
                            familiaId = remote.family_id.toString(),
                            participantesIds = emptyList(),
                            observaciones = remote.notes.orEmpty(),
                            estado = it,
                            ordenEspera = null
                        )
                    }
                }

                // Conservamos las inscripciones de otras familias
                // que pudieran estar presentes en el estado local.
                estado = estado.copy(
                    inscripciones = estado.inscripciones.filterNot {
                        it.familiaId == familiaId
                    } + inscripcionesLocales
                )

                // 2. Consultar las encuestas ya enviadas.
                encuestasEnviadas = surveyRepository.getMySurveys(token)

            } catch (e: Exception) {
                errorEncuestas = e.message
                    ?: "No se pudieron cargar tus inscripciones y encuestas."
            } finally {
                cargandoEncuestas = false
            }
        }
    }

    fun seleccionarEvento(id: String) {
        if (estado.eventos.none { it.id == id }) return

        eventoSeleccionadoId = id
        error = null
    }

    // Estas dos operaciones todavía utilizan el repositorio local.
    // Las conectaremos a POST /registrations y PATCH de cancelación
    // en el siguiente paso.

    fun inscribir(
        familiaId: String,
        participantes: List<String>,
        observaciones: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val activityId = eventoSeleccionadoId.toIntOrNull()
                    ?: throw IllegalStateException(
                        if (cargando) {
                            "Espera a que terminen de cargar las actividades."
                        } else if (estado.eventos.isEmpty()) {
                            "No hay actividades disponibles. Intenta actualizar la lista."
                        } else {
                            "Selecciona una actividad válida antes de inscribirte."
                        }
                    )

                val token = sessionManager.token.first()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                // Solo IDs reales de acompañantes.
                // El titular se cuenta automáticamente en el backend.
                val participantIds = participantes.map { id ->
                    id.toIntOrNull()
                        ?: throw IllegalArgumentException(
                            "Uno de los integrantes seleccionados tiene un ID inválido."
                        )
                }


                val inscripcionesExistentes =
                    registrationRepository.getMyRegistrations(token)

                val inscripcionActiva = inscripcionesExistentes.firstOrNull {
                    it.activity_id == activityId &&
                            !it.status.equals("cancelada", ignoreCase = true)
                }

                if (inscripcionActiva != null) {
                    val estadoRemoto = when (inscripcionActiva.status.uppercase()) {
                        "LISTA_ESPERA", "EN_ESPERA" ->
                            mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA

                        "CONFIRMADA" ->
                            mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA

                        else -> null
                    }

                    if (estadoRemoto != null) {
                        val inscripcionLocal =
                            mx.tec.familias.data.model.Inscripcion(
                                id = inscripcionActiva.id.toString(),
                                eventoId = activityId.toString(),
                                familiaId = familiaId,
                                participantesIds = emptyList(),
                                observaciones = inscripcionActiva.notes.orEmpty(),
                                estado = estadoRemoto,
                                ordenEspera = null
                            )

                        estado = estado.copy(
                            inscripciones = estado.inscripciones.filterNot {
                                it.eventoId == activityId.toString() &&
                                        it.familiaId == familiaId
                            } + inscripcionLocal
                        )
                    }

                    error = null
                    onResult(false, inscripcionActiva.status)
                    return@launch
                }

                val response = registrationRepository.createRegistration(
                    token = token,
                    request = RegistrationRequest(
                        activity_id = activityId,
                        participant_ids = participantIds,
                        notes = observaciones.trim().ifBlank { null }
                    )
                )


                val estadoInscripcionEnum = if (response.status == "lista_espera" || response.status == "EN_ESPERA") {
                    mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA
                } else {
                    mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA
                }

                val nuevaInscripcion = mx.tec.familias.data.model.Inscripcion(
                    id = response.id.toString(),
                    eventoId = activityId.toString(),
                    familiaId = familiaId,
                    participantesIds = participantes,
                    observaciones = observaciones,
                    estado = estadoInscripcionEnum,
                    ordenEspera = null
                )

                estado = estado.copy(
                    inscripciones = estado.inscripciones.filterNot {
                        it.eventoId == activityId.toString() && it.familiaId == familiaId
                    } + nuevaInscripcion
                )

                ultimaInscripcionId = response.id.toString()
                ultimaInscripcionEstado = response.status

                onResult(true, response.status)
            } catch (e: retrofit2.HttpException) {
                val errorDetail = try {
                    val errorJson = e.response()?.errorBody()?.string()
                    if (!errorJson.isNullOrBlank()) {
                        val jsonObject = org.json.JSONObject(errorJson)
                        if (jsonObject.has("detail")) jsonObject.getString("detail") else null
                    } else null
                } catch (_: Exception) {
                    null
                }

                error = errorDetail ?: when (e.code()) {
                    409 -> "Tu familia ya está inscrita en esta actividad o la actividad ya no está disponible."
                    else -> "No se pudo completar la inscripción (Error ${e.code()})."
                }
                onResult(false, null)
            } catch (e: Exception) {
                error = e.message
                    ?: "No se pudo completar la inscripción."
                onResult(false, null)
            } finally {
                cargando = false
            }
        }
    }


    fun cancelarInscripcion(familiaId: String): Boolean {
        error = null

        val solicitud = estado.inscripciones.firstOrNull {
            it.eventoId == eventoSeleccionadoId &&
                    it.familiaId == familiaId &&
                    it.estado ==
                    mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA
        }

        if (solicitud == null) {
            error = "No tienes una inscripción confirmada en esta actividad."
            return false
        }

        return try {
            val cancelada = repositorio.cancelar(
                solicitud.id,
                familiaId
            )

            estado = repositorio.consultar()

            if (!cancelada) {
                error = "La inscripción ya no está disponible."
            }

            if (cancelada && ultimaInscripcionId == solicitud.id) {
                ultimaInscripcionId = null
            }

            cancelada
        } catch (e: IllegalArgumentException) {
            error = e.message ?: "No se pudo cancelar la inscripción."
            false
        }
    }

    private fun parseDateTime(value: String?): LocalDateTime? {
        if (value.isNullOrBlank()) return null

        return runCatching {
            Instant.parse(value)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }.getOrNull() ?: runCatching {
            OffsetDateTime.parse(value)
                .atZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()
        }.getOrNull() ?: runCatching {
            LocalDateTime.parse(value)
        }.getOrNull()
    }

    private fun parseEpochMillis(value: String?): Long {
        if (value.isNullOrBlank()) return 0L

        return runCatching {
            Instant.parse(value).toEpochMilli()
        }.getOrNull() ?: runCatching {
            OffsetDateTime.parse(value).toInstant().toEpochMilli()
        }.getOrNull() ?: runCatching {
            LocalDateTime.parse(value)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrDefault(0L)
    }

    fun enviarEncuesta(
        registrationId: String,
        campaignRating: Int,
        organizationRating: Int,
        communicationRating: Int,
        overallRating: Int,
        comment: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            cargandoEncuestas = true
            errorEncuestas = null

            try {
                val token = sessionManager.token.first()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                val idReal = registrationId.toIntOrNull()
                    ?: throw IllegalArgumentException(
                        "El ID de la inscripción no es válido."
                    )

                val calificaciones = listOf(
                    campaignRating,
                    organizationRating,
                    communicationRating,
                    overallRating
                )

                require(calificaciones.all { it in 1..5 }) {
                    "Todas las calificaciones deben estar entre 1 y 5."
                }

                if (encuestasEnviadas.any {
                        it.registration_id == idReal
                    }
                ) {
                    throw IllegalStateException(
                        "Ya enviaste una encuesta para esta inscripción."
                    )
                }

                val respuesta = surveyRepository.createSurvey(
                    token = token,
                    request = SurveyCreateRequest(
                        registration_id = idReal,
                        campaign_rating = campaignRating,
                        organization_rating = organizationRating,
                        communication_rating = communicationRating,
                        overall_rating = overallRating,
                        comment = comment.trim().ifBlank { null },
                        photo_url = null,
                        photo_consent = false
                    )
                )

                encuestasEnviadas = encuestasEnviadas + respuesta

                onResult(true, "¡Gracias por compartir tu experiencia!")

            } catch (e: retrofit2.HttpException) {
                val detalle = try {
                    val body = e.response()?.errorBody()?.string()

                    if (!body.isNullOrBlank()) {
                        val json = org.json.JSONObject(body)
                        if (json.has("detail")) {
                            json.getString("detail")
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } catch (_: Exception) {
                    null
                }

                val mensaje = detalle
                    ?: "No se pudo enviar la encuesta (Error ${e.code()})."

                errorEncuestas = mensaje
                onResult(false, mensaje)

            } catch (e: Exception) {
                val mensaje = e.message ?: "No se pudo enviar la encuesta."
                errorEncuestas = mensaje
                onResult(false, mensaje)

            } finally {
                cargandoEncuestas = false
            }
        }
    }
}

class ActividadesViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ActividadesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ActividadesViewModel(sessionManager) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}

