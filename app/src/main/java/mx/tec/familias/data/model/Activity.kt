package mx.tec.familias.data.model

data class Activity(
    val id: String,
    val campaignId: Int? = null,
    val nombre: String,
    val organizacion: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val lugar: String,
    val capacidadPersonas: Int,
    val cierreInscripciones: Long,
    val personasConfirmadas: Int = 0,
    val url: String = "",
    val puntoEncuentro: String = "",
    val tipoAportacion: String = "",
    val requisitos: String = "",
    val contacto: String = "",
    val endsAtEpochMillis: Long? = null
)

enum class EstadoInscripcion {
    CONFIRMADA,
    EN_ESPERA
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
    fun eventoOrNull(id: String): Activity? =
        eventos.firstOrNull { it.id == id }

    fun evento(id: String): Activity =
        eventoOrNull(id) ?: eventos.firstOrNull() ?: Activity(
            id = id,
            campaignId = null,
            nombre = "Actividad",
            organizacion = "Organización",
            descripcion = "",
            fecha = "",
            hora = "",
            lugar = "",
            capacidadPersonas = 0,
            cierreInscripciones = 0L,
            personasConfirmadas = 0,
            url = "",
            puntoEncuentro = "",
            tipoAportacion = "",
            requisitos = "",
            contacto = ""
        )

    /** Personas confirmadas reportadas por el backend. */

    fun ocupados(id: String): Int {
        val ev = eventoOrNull(id) ?: return 0
        return ev.personasConfirmadas
    }

    fun disponibles(id: String): Int {
        val ev = eventoOrNull(id) ?: return 0
        return (ev.capacidadPersonas - ocupados(id)).coerceAtLeast(0)
    }

    fun posicion(inscripcion: Inscripcion): Int =
        inscripciones.count {
            it.eventoId == inscripcion.eventoId &&
                    it.estado == EstadoInscripcion.EN_ESPERA &&
                    (it.ordenEspera ?: Long.MAX_VALUE) <
                    (inscripcion.ordenEspera ?: Long.MAX_VALUE)
        } + 1
}