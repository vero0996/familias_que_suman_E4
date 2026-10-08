package mx.tec.familias.data.repository

import mx.tec.familias.data.model.*
import java.util.UUID

/** Repositorio compartido en memoria para el prototipo, con cupo por familia.
 * No persiste al terminar el proceso ni sincroniza entre teléfonos.
 * Un backend deberá reemplazar el monitor por una transacción en base de datos.
 */
class ActividadesRepository(
    private val eventos: List<Activity>,
    private val reloj: () -> Long = System::currentTimeMillis
) {
    private val inscripciones = mutableListOf<Inscripcion>()
    private var siguienteOrden = 0L

    init {
        require(eventos.map { it.id }.distinct().size == eventos.size)
        require(eventos.all { it.capacidadFamilias > 0 })
    }

    @Synchronized
    fun consultar(): EstadoActividades = EstadoActividades(eventos.toList(), inscripciones.toList())

    @Synchronized
    fun inscribir(eventoId: String, familiaId: String,
                  participantesIds: List<String>, observaciones: String = ""): Inscripcion {
        require(familiaId.isNotBlank()) { "Registra tu familia antes de inscribirte." }
        require(participantesIds.isNotEmpty() && participantesIds.all { it.isNotBlank() }) {
            "Selecciona al menos un participante."
        }
        val evento = eventos.firstOrNull { it.id == eventoId }
            ?: throw IllegalArgumentException("La actividad no existe.")
        // Los reintentos y dobles pulsaciones devuelven la misma solicitud.
        inscripciones.firstOrNull {
            it.eventoId == eventoId && it.familiaId == familiaId
        }?.let { return it }
        require(reloj() < evento.cierreInscripciones) { "Las inscripciones están cerradas." }
        val confirmadas = inscripciones.count {
            it.eventoId == eventoId && it.estado == EstadoInscripcion.CONFIRMADA
        }
        val confirmada = confirmadas < evento.capacidadFamilias
        val nueva = Inscripcion(
            UUID.randomUUID().toString(), eventoId, familiaId,
            participantesIds.distinct().toList(), observaciones.trim(),
            if (confirmada) EstadoInscripcion.CONFIRMADA else EstadoInscripcion.EN_ESPERA,
            if (confirmada) null else ++siguienteOrden
        )
        inscripciones.add(nueva)
        return nueva
    }

    /** Cancela solamente la solicitud confirmada indicada de su familia.
     * La baja y la promoción FIFO se hacen en la misma sección sincronizada.
     * El prototipo elimina el registro cancelado; no conserva historial.
     */
    @Synchronized
    fun cancelar(inscripcionId: String, familiaId: String): Boolean {
        val indice = inscripciones.indexOfFirst { it.id == inscripcionId }
        if (indice < 0) return false
        val actual = inscripciones[indice]
        require(actual.familiaId == familiaId && familiaId.isNotBlank()) {
            "La inscripción no pertenece a tu familia."
        }
        require(actual.estado == EstadoInscripcion.CONFIRMADA) {
            "Solo puedes cancelar una inscripción confirmada."
        }
        inscripciones.removeAt(indice)
        val evento = eventos.first { it.id == actual.eventoId }
        if (reloj() < evento.cierreInscripciones) {
            val siguiente = inscripciones.filter {
                it.eventoId == evento.id && it.estado == EstadoInscripcion.EN_ESPERA
            }.minByOrNull { it.ordenEspera ?: Long.MAX_VALUE }
            if (siguiente != null) {
                val posicion = inscripciones.indexOfFirst { it.id == siguiente.id }
                inscripciones[posicion] = siguiente.copy(
                    estado = EstadoInscripcion.CONFIRMADA,
                    ordenEspera = null
                )
            }
        }
        return true
    }

    companion object {
        fun prototipo(): ActividadesRepository {
            // 24/25 noviembre 2026, 09:00 en Monterrey (UTC-6).
            val repo = ActividadesRepository(listOf(
                Activity("arboles", "Plantación de Árboles en El Pardo", "Asociación Bosque Vivo",
                    "Plantaremos árboles nativos y aprenderemos a cuidar nuestros espacios naturales.",
                    "24 NOV 2026", "09:00 AM – 12:00 PM", "Parque El Pardo", 2, 1795532400000L),
                Activity("lectura", "Lectura Compartida", "Fundación Aprender Juntos",
                    "Comparte lecturas y actividades con otras familias de la comunidad.",
                    "25 NOV 2026", "09:00 AM – 11:00 AM", "Centro Comunitario", 3, 1795618800000L)
            ))
            // Datos explícitos de demostración: árboles lleno y lectura con cupo.
            if (System.currentTimeMillis() < 1795532400000L) {
                repo.inscribir("arboles", "demo:A", listOf("demo:A:titular"))
                repo.inscribir("arboles", "demo:B", listOf("demo:B:titular"))
            }
            if (System.currentTimeMillis() < 1795618800000L) {
                repo.inscribir("lectura", "demo:A", listOf("demo:A:titular"))
            }
            return repo
        }
    }
}