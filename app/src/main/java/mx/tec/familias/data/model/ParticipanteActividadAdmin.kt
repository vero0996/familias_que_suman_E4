package mx.tec.familias.data.model

data class ParticipanteActividadAdmin(
    val id: Int,
    val nombreFamilia: String,
    val correo: String,
    val telefono: String,
    val integrantes: List<String>,
    val estado: EstadoParticipante = EstadoParticipante.CONFIRMADO,
    val lugaresAsignados: List<Int> = emptyList()
)

enum class EstadoParticipante {
    CONFIRMADO,
    LISTA_ESPERA
}