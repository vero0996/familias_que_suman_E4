
package mx.tec.familias.data.repository

import mx.tec.familias.data.model.*
import java.util.UUID

/**
 * Repositorio en memoria para el prototipo.
 *
 * Las inscripciones locales no se sincronizan entre teléfonos
 * ni se guardan al cerrar la aplicación.
 */
class ActividadesRepository(
    private val eventos: List<Activity>,
    private val reloj: () -> Long = System::currentTimeMillis
) {
    private val inscripciones = mutableListOf<Inscripcion>()
    private var siguienteOrden = 0L

    init {
        require(eventos.map { it.id }.distinct().size == eventos.size) {
            "Los IDs de las actividades deben ser únicos."
        }
        require(eventos.all {
            it.capacidadPersonas > 0 &&
                    it.ocupacionInicial in 0..it.capacidadPersonas
        }) {
            "Revisa la capacidad y la ocupación inicial de las actividades."
        }
    }

    @Synchronized
    fun consultar(): EstadoActividades =
        EstadoActividades(eventos.toList(), inscripciones.toList())

    @Synchronized
    fun inscribir(
        eventoId: String,
        familiaId: String,
        participantesIds: List<String>,
        observaciones: String = ""
    ): Inscripcion {
        require(familiaId.isNotBlank()) {
            "Registra tu familia antes de inscribirte."
        }

        val participantes = participantesIds
            .filter { it.isNotBlank() }
            .distinct()

        require(participantes.isNotEmpty()) {
            "Selecciona al menos un participante."
        }

        val evento = eventos.firstOrNull { it.id == eventoId }
            ?: throw IllegalArgumentException("La actividad no existe.")

        // Evita duplicar solicitudes por dobles pulsaciones.
        inscripciones.firstOrNull {
            it.eventoId == eventoId && it.familiaId == familiaId
        }?.let { return it }

        require(reloj() < evento.cierreInscripciones) {
            "Las inscripciones están cerradas."
        }

        val estadoActual = consultar()
        val disponibles = estadoActual.disponibles(eventoId)

        require(participantes.size <=
                evento.capacidadPersonas - evento.ocupacionInicial) {
            "El número de participantes supera la capacidad disponible de la actividad."
        }

        val confirmada = participantes.size <= disponibles

        val nueva = Inscripcion(
            id = UUID.randomUUID().toString(),
            eventoId = eventoId,
            familiaId = familiaId,
            participantesIds = participantes,
            observaciones = observaciones.trim(),
            estado = if (confirmada) {
                EstadoInscripcion.CONFIRMADA
            } else {
                EstadoInscripcion.EN_ESPERA
            },
            ordenEspera = if (confirmada) null else ++siguienteOrden
        )

        inscripciones.add(nueva)
        return nueva
    }

    /**
     * Cancela una inscripción confirmada y promueve una familia
     * de la lista de espera si sus participantes caben en los lugares
     * liberados. Se conserva el orden de espera entre las familias
     * que sí caben.
     */
    @Synchronized
    fun cancelar(
        inscripcionId: String,
        familiaId: String
    ): Boolean {
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
            val disponibles = consultar().disponibles(evento.id)

            val siguiente = inscripciones
                .filter {
                    it.eventoId == evento.id &&
                            it.estado == EstadoInscripcion.EN_ESPERA &&
                            it.participantesIds.size <= disponibles
                }
                .minByOrNull { it.ordenEspera ?: Long.MAX_VALUE }

            if (siguiente != null) {
                val posicion = inscripciones.indexOfFirst {
                    it.id == siguiente.id
                }

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
            return ActividadesRepository(
                eventos = listOf(
                    Activity(
                        id = "familia_misionera_octubre",
                        nombre = "Familia Misionera Octubre",
                        organizacion = "Familias que Suman",
                        descripcion = "Visita y acompaña a familias de la comunidad. En esta edición, la misión se centra en los Santos.",
                        fecha = "24 Oct",
                        hora = "09:30 AM – 12:30 PM",
                        lugar = "Bahía de La Paz 223, San Gilberto, Santa Catarina, N.L.",
                        capacidadPersonas = 25,
                        cierreInscripciones = 1792855800000L,
                        ocupacionInicial = 1,
                        url = "https://familiasquesuman.com/actividades/6ac5552deaa491db4aeb8e66",
                        puntoEncuentro = "Parroquia Cristo Resucitado, Santa Catarina",
                        tipoAportacion = "Sin aportación económica",
                        requisitos = "Llevar playera de Familia Misionera si tienes; de lo contrario, ropa apropiada. Llevar hidratación.",
                        contacto = "Maru Madero por WhatsApp"
                    ),
                    Activity(
                        id = "regalando_estrellas_materno",
                        nombre = "Regalando Estrellas: Visita al Materno Infantil",
                        organizacion = "Familias que Suman",
                        descripcion = "Visita al Hospital Materno Infantil para acompañar a pacientes y entregar desayunos y kits de higiene a nuevas mamás.",
                        fecha = "7 Nov",
                        hora = "09:45 AM – 11:30 AM",
                        lugar = "Av. San Rafael No. 450, Guadalupe, N.L.",
                        capacidadPersonas = 10,
                        cierreInscripciones = 1794066300000L,
                        ocupacionInicial = 0,
                        url = "https://familiasquesuman.com/actividades/6ac5535fb4def8b6f73e4887",
                        puntoEncuentro = "Explanada del Hospital Materno Infantil",
                        tipoAportacion = "En especie",
                        requisitos = "Familias con niñas y niños mayores de 7 años. Ropa cómoda, blusa blanca sencilla y tenis. Coordinar la aportación con la organización; las opciones incluyen fruta, sándwiches, galletas y otros alimentos.",
                        contacto = "Familias que Suman"
                    ),
                    Activity(
                        id = "familia_misionera_noviembre",
                        nombre = "Familia Misionera Noviembre",
                        organizacion = "Familias que Suman",
                        descripcion = "Visita y acompaña a familias de la comunidad durante la misión familiar de noviembre.",
                        fecha = "28 Nov",
                        hora = "09:30 AM – 12:30 PM",
                        lugar = "Bahía de La Paz 223, San Gilberto, Santa Catarina, N.L.",
                        capacidadPersonas = 25,
                        cierreInscripciones = 1795879800000L,
                        ocupacionInicial = 0,
                        url = "https://familiasquesuman.com/actividades/6ac5559b684c93540ba08c06",
                        puntoEncuentro = "Parroquia Cristo Resucitado, Santa Catarina",
                        tipoAportacion = "Sin aportación económica",
                        requisitos = "Llevar playera de Familia Misionera si tienes; de lo contrario, ropa apropiada. Llevar hidratación.",
                        contacto = "Maru Madero por WhatsApp"
                    ),
                    Activity(
                        id = "posada_sendero",
                        nombre = "Posada Sendero",
                        organizacion = "Familias que Suman",
                        descripcion = "Celebración navideña con familias de Arroyo en la colonia Sendero, con juegos, regalos, refrigerio y convivencia.",
                        fecha = "28 Nov",
                        hora = "09:30 AM – 12:00 PM",
                        lugar = "San Ildefonso S/N, Colonia Sendero, Santa Catarina, N.L.",
                        capacidadPersonas = 15,
                        cierreInscripciones = 1795879800000L,
                        ocupacionInicial = 7,
                        url = "https://familiasquesuman.com/actividades/6a70cbdea94b0c514b35db0e",
                        puntoEncuentro = "Centro Comunitario",
                        tipoAportacion = "En especie",
                        requisitos = "Llevar ropa cómoda y sencilla. La organización reúne aportaciones para alimentos, juguetes, pasteles y otros elementos de la posada.",
                        contacto = "Familias que Suman"
                    ),
                    Activity(
                        id = "familia_misionera_posada",
                        nombre = "Familia Misionera Posada",
                        organizacion = "Familias que Suman",
                        descripcion = "Misión familiar de diciembre para visitar y acompañar a familias de la comunidad durante la temporada navideña.",
                        fecha = "19 Dec",
                        hora = "09:30 AM",
                        lugar = "Bahía de La Paz 223, San Gilberto, Santa Catarina, N.L.",
                        capacidadPersonas = 25,
                        cierreInscripciones = 1797694200000L,
                        ocupacionInicial = 0,
                        url = "https://familiasquesuman.com/actividades/6ac5559b684c93540ba08c06",
                        puntoEncuentro = "Parroquia Cristo Resucitado, Santa Catarina",
                        tipoAportacion = "Sin aportación económica",
                        requisitos = "Llevar ropa apropiada e hidratación.",
                        contacto = "Familias que Suman"
                    )
                )
            )
        }
    }
}
