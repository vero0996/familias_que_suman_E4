package mx.tec.familias.data.model

data class Inscripcion(
    val id: String,
    val actividad: String,
    val nombreUsuario: String,
    val correo: String,
    val telefono: String,
    val integrantes: List<String>,
    val observaciones: String,
    val estado: EstadoParticipante = EstadoParticipante.CONFIRMADO,
    val lugaresAsignados: List<Int> = emptyList()
)