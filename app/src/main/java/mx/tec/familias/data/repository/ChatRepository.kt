package mx.tec.familias.data.repository

import androidx.compose.runtime.mutableStateListOf
import mx.tec.familias.ui.screens.mensajes.Conversacion
import mx.tec.familias.ui.screens.mensajes.MensajeChat

object ChatRepository {
    val conversaciones = mutableStateListOf(
        Conversacion(
            id = 1,
            nombre = "Fundación Aprender Juntos",
            ultimoMensaje = "¡Hola! No olviden traer las materiales.",
            hora = "10:30 AM",
            mensajesSinLeer = 1
        ),
        Conversacion(
            id = 2,
            nombre = "Red Solidaria",
            ultimoMensaje = "Se ha confirmado el punto de encuentro.",
            hora = "Ayer",
            mensajesSinLeer = 0
        ),
        Conversacion(
            id = 3,
            nombre = "Asociación EcoVerde",
            ultimoMensaje = "Gracias por participar en la reforestación.",
            hora = "Lunes",
            mensajesSinLeer = 0
        )
    )

    private val mensajesMap = mutableMapOf<Int, MutableList<MensajeChat>>().apply {
        put(1, mutableStateListOf(
            MensajeChat(1, "¡Hola! No olviden traer las materiales.", "10:30 AM", false)
        ))
        put(2, mutableStateListOf(
            MensajeChat(1, "Se ha confirmado el punto de encuentro.", "Ayer", false)
        ))
        put(3, mutableStateListOf(
            MensajeChat(1, "Gracias por participar en la reforestación.", "Lunes", false)
        ))
    }

    fun getMensajes(conversacionId: Int): MutableList<MensajeChat> {
        return mensajesMap.getOrPut(conversacionId) {
            mutableStateListOf()
        }
    }

    fun enviarMensaje(conversacionId: Int, texto: String, enviadoPorUsuario: Boolean) {
        val lista = getMensajes(conversacionId)
        val nuevoId = (lista.maxOfOrNull { it.id } ?: 0) + 1
        lista.add(
            MensajeChat(
                id = nuevoId,
                texto = texto,
                hora = "Ahora",
                enviadoPorUsuario = enviadoPorUsuario
            )
        )
        conversaciones.find { it.id == conversacionId }?.let { conv ->
            val index = conversaciones.indexOf(conv)
            conversaciones[index] = conv.copy(
                ultimoMensaje = texto,
                hora = "Ahora"
            )
        }
    }
}
