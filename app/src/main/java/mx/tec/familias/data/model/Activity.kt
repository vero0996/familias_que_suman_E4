package mx.tec.familias.data.model

/** En esta versión cada solicitud ocupa UN cupo de familia. */
data class Activity(
    val id: String,
    val nombre: String,
    val organizacion: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val lugar: String,
    val capacidadFamilias: Int,
    val cierreInscripciones: Long
)

enum class EstadoInscripcion {
    CONFIRMADA, EN_ESPERA
}

data class Inscripcion(
    val id: String,
    val eventoId: String,
    val familiaId: String,
    val participantesIds: List<String>,
    val observaciones: String,
    val estado: EstadoInscripcion,
    val ordenEspera: Long?
)

data class EstadoActividades(
    val eventos: List<Activity>,
    val inscripciones: List<Inscripcion>
) {
    fun evento(id: String): Activity = eventos.first { it.id == id }
    fun ocupados(id: String): Int = inscripciones.count {
        it.eventoId == id && it.estado == EstadoInscripcion.CONFIRMADA
    }
    fun disponibles(id: String): Int = evento(id).capacidadFamilias - ocupados(id)

    fun posicion(inscripcion: Inscripcion): Int = inscripciones.count {
        it.eventoId == inscripcion.eventoId &&
            it.estado == EstadoInscripcion.EN_ESPERA &&
            (it.ordenEspera ?: Long.MAX_VALUE) < (inscripcion.ordenEspera ?: Long.MAX_VALUE)
    } + 1
}
