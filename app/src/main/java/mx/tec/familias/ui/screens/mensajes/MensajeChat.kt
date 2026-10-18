package mx.tec.familias.ui.screens.mensajes

data class MensajeChat(
    val id: Int,
    val texto: String,
    val hora: String,
    val enviadoPorUsuario: Boolean
)