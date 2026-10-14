
package mx.tec.familias.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.tec.familias.data.model.Activity
import mx.tec.familias.data.model.EstadoActividades
import mx.tec.familias.data.repository.ActividadesRepository
import mx.tec.familias.data.repository.ActivityRemoteRepository
import mx.tec.familias.data.repository.CampaignRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ActividadesViewModel : ViewModel() {

    private val repositorioRemoto = ActivityRemoteRepository()
    private val campaignRepository = CampaignRepository()

    private var repositorio = ActividadesRepository.prototipo()

    var estado by mutableStateOf(repositorio.consultar())
        private set

    var eventoSeleccionadoId by mutableStateOf("familia_misionera_octubre")
        private set

    var ultimaInscripcionId by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
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
                        organizacion = organizaciones[remote.campaign_id]
                            ?: "Organización",
                        descripcion = remote.description,
                        fecha = fechaInicio?.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ) ?: "",
                        hora = fechaInicio?.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ) ?: "",
                        lugar = remote.location,
                        capacidadPersonas = remote.capacity_families,
                        cierreInscripciones = parseEpochMillis(
                            remote.registration_deadline
                        ),
                        ocupacionInicial = 0,
                        url = "",
                        puntoEncuentro = remote.location,
                        tipoAportacion = "",
                        requisitos = "",
                        contacto = ""
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
        observaciones: String
    ): Boolean {
        error = null

        return try {
            ultimaInscripcionId = repositorio.inscribir(
                eventoSeleccionadoId,
                familiaId,
                participantes,
                observaciones
            ).id

            estado = repositorio.consultar()
            true
        } catch (e: IllegalArgumentException) {
            error = e.message ?: "No se pudo completar la solicitud."
            false
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
            OffsetDateTime.parse(value).toLocalDateTime()
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
}
