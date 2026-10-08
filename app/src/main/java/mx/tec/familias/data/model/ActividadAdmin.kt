package mx.tec.familias.data.model

data class ActividadAdmin(
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val ubicacion: String,
    val participantes: Int,
    val cuposTotales: Int,
    val esUrgente: Boolean = false,
    val imagenId: Int
)