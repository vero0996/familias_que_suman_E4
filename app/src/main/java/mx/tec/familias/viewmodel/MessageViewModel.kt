package mx.tec.familias.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import mx.tec.familias.data.remote.MessageResponse
import mx.tec.familias.data.remote.MessageUserResponse
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.repository.AuthRepository
import mx.tec.familias.data.repository.MessageRepository


class MessageViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val repository = MessageRepository()
    private val authRepository = AuthRepository()

    var usuarios by mutableStateOf<List<MessageUserResponse>>(emptyList())
        private set

    var mensajes by mutableStateOf<List<MessageResponse>>(emptyList())
        private set

    var usuarioActualId by mutableStateOf<Int?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set


    fun cargarUsuarios() {
        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val token = sessionManager.token.first()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                usuarios = repository.getMessageUsers(
                    token = token
                )

            } catch (e: Exception) {
                error = e.message
                    ?: "No se pudieron cargar los usuarios."
            } finally {
                cargando = false
            }
        }
    }


    fun cargarMensajes(userId: Int) {
        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val token = sessionManager.token.first()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                val usuario = authRepository.getCurrentUser(token)

                usuarioActualId = usuario.id

                mensajes = repository.getMessages(
                    token = token,
                    userId = userId
                )

            } catch (e: Exception) {
                error = e.message
                    ?: "No se pudieron cargar los mensajes."
            } finally {
                cargando = false
            }
        }
    }


    fun enviarMensaje(
        userId: Int,
        texto: String
    ) {
        if (texto.isBlank()) return

        viewModelScope.launch {
            error = null

            try {
                val token = sessionManager.token.first()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Tu sesión expiró. Inicia sesión nuevamente."
                    )

                val nuevoMensaje = repository.sendMessage(
                    token = token,
                    receiverUserId = userId,
                    text = texto.trim()
                )

                mensajes = mensajes + nuevoMensaje

            } catch (e: Exception) {
                error = e.message
                    ?: "No se pudo enviar el mensaje."
            }
        }
    }
}


class MessageViewModelFactory(
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(sessionManager) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}