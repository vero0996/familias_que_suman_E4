package mx.tec.familias.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mx.tec.familias.data.model.Activity
import mx.tec.familias.data.model.EstadoActividades
import mx.tec.familias.data.remote.RegistrationRequest
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.remote.SurveyCreateRequest
import mx.tec.familias.data.remote.SurveyResponse
import mx.tec.familias.data.repository.ActivityRemoteRepository
import mx.tec.familias.data.repository.CampaignRepository
import mx.tec.familias.data.repository.RegistrationRemoteRepository
import mx.tec.familias.data.repository.SurveyRemoteRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ActividadesViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val repositorioRemoto = ActivityRemoteRepository()
    private val campaignRepository = CampaignRepository()
    private val registrationRepository = RegistrationRemoteRepository()
    private val surveyRepository = SurveyRemoteRepository()

    var ultimaInscripcionEstado by mutableStateOf<String?>(null)
        private set

    var estado by mutableStateOf(EstadoActividades(emptyList(), emptyList()))
        private set

    var eventoSeleccionadoId by mutableStateOf("")

    var ultimaInscripcionId by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

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
                actualizarActividadesDesdeServidor()
            } catch (e: Exception) {
                error = e.message
                    ?: "No se pudieron cargar las actividades del servidor."
            } finally {
                cargando = false
            }
        }
    }

    private suspend fun actualizarActividadesDesdeServidor() {
        val campanias = runCatching {
            campaignRepository.getCampaigns()
        }.getOrDefault(emptyList())

        val organizaciones = campanias.associate {
            it.id to it.organization_name
        }

        val actividades = repositorioRemoto.getActivities().map { remote ->
            val fechaInicio = parseDateTime(remote.starts_at)

            Activity(
                id = remote.id.toString(),
                campaignId = remote.campaign_id,
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
                personasConfirmadas = remote.confirmed_people,
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

        val idsActividad = actividades.mapTo(mutableSetOf()) {
            it.id
        }

        estado = EstadoActividades(
            eventos = actividades,
            inscripciones = estado.inscripciones.filter {
                it.eventoId in idsActividad
            }
        )

        if (idsActividad.none { it == eventoSeleccionadoId }) {
            eventoSeleccionadoId = actividades.firstOrNull()?.id.orEmpty()
        }
    }

    fun cargarMisInscripcionesYEncuestas(
        familiaId: String
    ) {
        viewModelScope.launch {
            cargandoEncuestas = true
            errorEncuestas = null

            try {
                val token = sessionManager
                    .getToken()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: throw IllegalStateException(
                        "Inicia sesión para consultar tus encuestas."
                    )

                // 1. Cargar las inscripciones reales de la familia.
                val inscripcionesRemotas =
                    registrationRepository.getMyRegistrations(token)

                val inscripcionesLocales =
                    inscripcionesRemotas.mapNotNull { remote ->

                        val estadoRemoto =
                            when (remote.status.uppercase()) {
                                "CONFIRMADA" ->
                                    mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA

                                "EN_ESPERA",
                                "LISTA_ESPERA" ->
                                    mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA

                                else -> null
                            }

                        estadoRemoto?.let {
                            mx.tec.familias.data.model.Inscripcion(
                                id = remote.id.toString(),
                                eventoId = remote.activity_id.toString(),
                                familiaId = familiaId,
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
                    inscripciones =
                        estado.inscripciones.filterNot {
                            it.familiaId == familiaId
                        } + inscripcionesLocales
                )

                // 2. Consultar las encuestas ya enviadas.
                encuestasEnviadas =
                    surveyRepository.getMySurveys(token)

            } catch (e: Exception) {
                errorEncuestas = e.message
                    ?: "No se pudieron cargar tus inscripciones y encuestas."
            } finally {
                cargandoEncuestas = false
            }
        }
    }

    fun seleccionarEvento(
        id: String
    ) {
        if (
            estado.eventos.none {
                it.id == id
            }
        ) {
            return
        }

        eventoSeleccionadoId = id
        error = null
    }

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
                val activityId =
                    eventoSeleccionadoId.toIntOrNull()
                        ?: throw IllegalStateException(
                            if (cargando) {
                                "Espera a que terminen de cargar las actividades."
                            } else if (estado.eventos.isEmpty()) {
                                "No hay actividades disponibles. Intenta actualizar la lista."
                            } else {
                                "Selecciona una actividad válida antes de inscribirte."
                            }
                        )

                val token = sessionManager
                    .getToken()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                val participantIds =
                    participantes.map { id ->
                        id.toIntOrNull()
                            ?: throw IllegalArgumentException(
                                "Uno de los integrantes seleccionados tiene un ID inválido."
                            )
                    }

                val inscripcionesExistentes =
                    registrationRepository.getMyRegistrations(
                        token
                    )

                val inscripcionActiva =
                    inscripcionesExistentes.firstOrNull {
                        it.activity_id == activityId &&
                                !it.status.equals(
                                    "cancelada",
                                    ignoreCase = true
                                )
                    }

                if (inscripcionActiva != null) {
                    val estadoRemoto =
                        when (
                            inscripcionActiva.status.uppercase()
                        ) {
                            "LISTA_ESPERA",
                            "EN_ESPERA" ->
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
                                observaciones =
                                    inscripcionActiva.notes.orEmpty(),
                                estado = estadoRemoto,
                                ordenEspera = null
                            )

                        estado = estado.copy(
                            inscripciones =
                                estado.inscripciones.filterNot {
                                    it.eventoId ==
                                            activityId.toString() &&
                                            it.familiaId ==
                                            familiaId
                                } + inscripcionLocal
                        )
                    }

                    error = null

                    onResult(
                        false,
                        inscripcionActiva.status
                    )

                    return@launch
                }

                val response =
                    registrationRepository.createRegistration(
                        token = token,
                        request =
                            RegistrationRequest(
                                activity_id = activityId,
                                participant_ids = participantIds,
                                notes =
                                    observaciones
                                        .trim()
                                        .ifBlank {
                                            null
                                        }
                            )
                    )

                val estadoNormalizado =
                    response.status
                        .trim()
                        .uppercase(
                            java.util.Locale.ROOT
                        )

                val estadoInscripcionEnum =
                    when (estadoNormalizado) {
                        "LISTA_ESPERA",
                        "EN_ESPERA" ->
                            mx.tec.familias.data.model.EstadoInscripcion.EN_ESPERA

                        "CONFIRMADA" ->
                            mx.tec.familias.data.model.EstadoInscripcion.CONFIRMADA

                        else -> {
                            throw IllegalStateException(
                                "El backend devolvió un estado de inscripción desconocido: ${response.status}"
                            )
                        }
                    }

                val nuevaInscripcion =
                    mx.tec.familias.data.model.Inscripcion(
                        id = response.id.toString(),
                        eventoId = activityId.toString(),
                        familiaId = familiaId,
                        participantesIds = participantes,
                        observaciones = observaciones,
                        estado = estadoInscripcionEnum,
                        ordenEspera = null
                    )

                estado = estado.copy(
                    inscripciones =
                        estado.inscripciones.filterNot {
                            it.eventoId ==
                                    activityId.toString() &&
                                    it.familiaId ==
                                    familiaId
                        } + nuevaInscripcion
                )

                ultimaInscripcionId =
                    response.id.toString()

                ultimaInscripcionEstado =
                    response.status

                // Actualizamos los cupos del servidor después
                // de guardar correctamente la inscripción.
                try {
                    actualizarActividadesDesdeServidor()
                } catch (refreshError: Exception) {
                    Log.e(
                        "ActivityOccupancy",
                        "La inscripción $activityId se guardó, pero no se pudo actualizar la ocupación.",
                        refreshError
                    )

                    error =
                        "La inscripción se guardó, pero no se pudieron actualizar los cupos. Intenta actualizar la lista."
                }

                onResult(
                    true,
                    response.status
                )

            } catch (e: retrofit2.HttpException) {
                val errorDetail =
                    try {
                        val body =
                            e.response()
                                ?.errorBody()
                                ?.string()

                        if (body.isNullOrBlank()) {
                            null
                        } else {
                            val detail =
                                org.json.JSONObject(body)
                                    .opt("detail")

                            (detail as? String)
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                        }

                    } catch (_: Exception) {
                        null
                    }

                error =
                    when (e.code()) {
                        422 ->
                            errorDetail
                                ?: "Revisa los acompañantes seleccionados y las observaciones."

                        401 ->
                            "Tu sesión expiró. Inicia sesión nuevamente."

                        403 ->
                            "No tienes permiso para realizar esta inscripción."

                        409 ->
                            errorDetail
                                ?: "Tu familia ya está inscrita o la actividad no está disponible."

                        else ->
                            errorDetail
                                ?: "No se pudo completar la inscripción (Error ${e.code()})."
                    }

                onResult(
                    false,
                    null
                )

            } catch (e: Exception) {
                error =
                    e.message
                        ?: "No se pudo completar la inscripción."

                onResult(
                    false,
                    null
                )

            } finally {
                cargando = false
            }
        }
    }

    fun cancelarInscripcion(
        familiaId: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val token = sessionManager
                    .getToken()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                val activityId =
                    eventoSeleccionadoId.toIntOrNull()
                        ?: throw IllegalStateException(
                            "Selecciona una actividad válida."
                        )

                val misInscripciones =
                    registrationRepository
                        .getMyRegistrations(
                            token
                        )

                val inscripcionActiva =
                    misInscripciones.firstOrNull {
                        it.activity_id ==
                                activityId &&
                                !it.status.equals(
                                    "cancelada",
                                    ignoreCase = true
                                )
                    }
                        ?: throw IllegalStateException(
                            "No tienes una inscripción activa en esta actividad."
                        )

                val registrationId =
                    inscripcionActiva.id

                registrationRepository.cancelRegistration(
                    token = token,
                    registrationId = registrationId,
                    reason = "Cancelada por la familia"
                )

                // Actualizamos la ocupación real de la actividad.
                try {
                    actualizarActividadesDesdeServidor()
                } catch (refreshError: Exception) {
                    Log.e(
                        "ActivityOccupancy",
                        "La cancelación $registrationId se confirmó, pero no se pudo actualizar la ocupación.",
                        refreshError
                    )

                    error =
                        "La cancelación se confirmó, pero no se pudieron actualizar los cupos. Intenta actualizar la lista."
                }

                cargarMisInscripcionesYEncuestas(
                    familiaId
                )

                if (
                    ultimaInscripcionId ==
                    registrationId.toString()
                ) {
                    ultimaInscripcionId = null
                    ultimaInscripcionEstado = null
                }

                onResult(
                    true,
                    "Inscripción cancelada correctamente."
                )

            } catch (e: Exception) {
                error =
                    e.message
                        ?: "No se pudo cancelar la inscripción."

                onResult(
                    false,
                    error
                )

            } finally {
                cargando = false
            }
        }
    }

    private fun parseDateTime(
        value: String?
    ): LocalDateTime? {
        if (value.isNullOrBlank()) {
            return null
        }

        return runCatching {
            Instant.parse(value)
                .atZone(
                    ZoneId.systemDefault()
                )
                .toLocalDateTime()

        }.getOrNull()
            ?: runCatching {
                OffsetDateTime.parse(value)
                    .atZoneSameInstant(
                        ZoneId.systemDefault()
                    )
                    .toLocalDateTime()

            }.getOrNull()
            ?: runCatching {
                LocalDateTime.parse(value)

            }.getOrNull()
    }

    private fun parseEpochMillis(
        value: String?
    ): Long {
        if (value.isNullOrBlank()) {
            return 0L
        }

        return runCatching {
            Instant.parse(value)
                .toEpochMilli()

        }.getOrNull()
            ?: runCatching {
                OffsetDateTime.parse(value)
                    .toInstant()
                    .toEpochMilli()

            }.getOrNull()
            ?: runCatching {
                LocalDateTime.parse(value)
                    .atZone(
                        ZoneId.systemDefault()
                    )
                    .toInstant()
                    .toEpochMilli()

            }.getOrDefault(
                0L
            )
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
                val token = sessionManager
                    .getToken()
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                val idReal =
                    registrationId.toIntOrNull()
                        ?: throw IllegalArgumentException(
                            "El ID de la inscripción no es válido."
                        )

                val calificaciones =
                    listOf(
                        campaignRating,
                        organizationRating,
                        communicationRating,
                        overallRating
                    )

                require(
                    calificaciones.all {
                        it in 1..5
                    }
                ) {
                    "Todas las calificaciones deben estar entre 1 y 5."
                }

                if (
                    encuestasEnviadas.any {
                        it.registration_id ==
                                idReal
                    }
                ) {
                    throw IllegalStateException(
                        "Ya enviaste una encuesta para esta inscripción."
                    )
                }

                val respuesta =
                    surveyRepository.createSurvey(
                        token = token,
                        request =
                            SurveyCreateRequest(
                                registration_id =
                                    idReal,
                                campaign_rating =
                                    campaignRating,
                                organization_rating =
                                    organizationRating,
                                communication_rating =
                                    communicationRating,
                                overall_rating =
                                    overallRating,
                                comment =
                                    comment
                                        .trim()
                                        .ifBlank {
                                            null
                                        },
                                photo_url = null,
                                photo_consent = false
                            )
                    )

                encuestasEnviadas =
                    encuestasEnviadas +
                            respuesta

                onResult(
                    true,
                    "¡Gracias por compartir tu experiencia!"
                )

            } catch (e: retrofit2.HttpException) {
                val detalle =
                    try {
                        val body =
                            e.response()
                                ?.errorBody()
                                ?.string()

                        if (!body.isNullOrBlank()) {
                            val json =
                                org.json.JSONObject(
                                    body
                                )

                            if (
                                json.has(
                                    "detail"
                                )
                            ) {
                                json.getString(
                                    "detail"
                                )
                            } else {
                                null
                            }

                        } else {
                            null
                        }

                    } catch (_: Exception) {
                        null
                    }

                val mensaje =
                    detalle
                        ?: "No se pudo enviar la encuesta (Error ${e.code()})."

                errorEncuestas =
                    mensaje

                onResult(
                    false,
                    mensaje
                )

            } catch (e: Exception) {
                val mensaje =
                    e.message
                        ?: "No se pudo enviar la encuesta."

                errorEncuestas =
                    mensaje

                onResult(
                    false,
                    mensaje
                )

            } finally {
                cargandoEncuestas = false
            }
        }
    }

    fun subirFotoCampania(
        context: Context,
        uri: Uri
    ) {
        viewModelScope.launch(
            Dispatchers.IO
        ) {
            try {
                // 1. Convertir el Uri de la galería a un archivo físico
                val inputStream =
                    context.contentResolver
                        .openInputStream(
                            uri
                        )

                val file =
                    File(
                        context.cacheDir,
                        "foto_temp.jpg"
                    )

                val outputStream =
                    FileOutputStream(
                        file
                    )

                inputStream?.copyTo(
                    outputStream
                )

                inputStream?.close()
                outputStream.close()

                // 2. Empaquetar el archivo
                val requestFile =
                    file.asRequestBody(
                        "image/jpeg"
                            .toMediaTypeOrNull()
                    )

                val body =
                    MultipartBody.Part
                        .createFormData(
                            "file",
                            file.name,
                            requestFile
                        )

                // 3. Enviar al servidor
                // val apiService = FamiliasApiService.create()
                // val response = apiService.uploadFoto(body)
                // if (response.isSuccessful) {
                //     println("Foto subida: ${response.body()?.url}")
                // }

            } catch (e: Exception) {
                e.printStackTrace()
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
        if (
            modelClass.isAssignableFrom(
                ActividadesViewModel::class.java
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return ActividadesViewModel(
                sessionManager
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}