package mx.tec.familias.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mx.tec.familias.data.model.FamilyMember
import mx.tec.familias.data.model.UserProfile
import mx.tec.familias.data.repository.FamilyMemberRemoteRepository

class FamilyViewModel : ViewModel() {

    private val familyMemberRepository = FamilyMemberRemoteRepository()

    // Identidad de la familia en el prototipo.
    val familiaId: String
        get() = usuario.value?.correo
            ?.trim()
            ?.lowercase(java.util.Locale.ROOT)
            ?.let { "familia:$it" }
            ?: ""

    var usuario = mutableStateOf<UserProfile?>(null)
        private set

    var accessToken = mutableStateOf<String?>(null)
        private set

    val integrantes = mutableStateListOf<FamilyMember>()

    // Estados para que las pantallas puedan mostrar carga y errores.
    var cargandoIntegrantes = mutableStateOf(false)
        private set

    var guardandoIntegrante = mutableStateOf(false)
        private set

    var errorIntegrantes = mutableStateOf<String?>(null)
        private set

    fun guardarSesion(token: String) {
        accessToken.value = token
        cargarIntegrantes()
    }

    fun cerrarSesion() {
        accessToken.value = null
        usuario.value = null
        integrantes.clear()
        errorIntegrantes.value = null
        cargandoIntegrantes.value = false
        guardandoIntegrante.value = false
    }

    fun guardarUsuario(
        nombre: String,
        correo: String,
        telefono: String
    ) {
        usuario.value = UserProfile(
            nombre = nombre.trim(),
            correo = correo.trim(),
            telefono = telefono.trim()
        )
    }

    fun guardarUsuarioAutenticado(
        nombre: String,
        correo: String,
        telefono: String?
    ) {
        guardarUsuario(
            nombre = nombre,
            correo = correo,
            telefono = telefono.orEmpty()
        )
    }

    // Recupera los integrantes de la familia desde el backend.
    fun cargarIntegrantes() {
        val token = accessToken.value

        if (token.isNullOrBlank()) {
            errorIntegrantes.value = "Inicia sesión para consultar tus integrantes."
            return
        }

        viewModelScope.launch {
            cargandoIntegrantes.value = true
            errorIntegrantes.value = null

            try {
                val integrantesRemotos =
                    familyMemberRepository.getFamilyMembers(token)

                integrantes.clear()
                integrantes.addAll(integrantesRemotos)
            } catch (e: Exception) {
                errorIntegrantes.value =
                    e.message ?: "No se pudieron cargar los integrantes."
            } finally {
                cargandoIntegrantes.value = false
            }
        }
    }

    // Registra el integrante en el backend.
    // Conservamos la firma para minimizar cambios en las pantallas actuales.


    fun agregarIntegrante(
        nombre: String,
        fechaNacimiento: String,
        parentesco: String,
        onSuccess: () -> Unit = {}
    ) {
        val token = accessToken.value

        if (token.isNullOrBlank()) {
            errorIntegrantes.value = "Inicia sesión para registrar un integrante."
            return
        }

        if (nombre.isBlank() || fechaNacimiento.isBlank() || parentesco.isBlank()) {
            errorIntegrantes.value = "Completa todos los campos."
            return
        }

        if (guardandoIntegrante.value) return

        viewModelScope.launch {
            guardandoIntegrante.value = true
            errorIntegrantes.value = null

            try {
                val nuevoIntegrante = familyMemberRepository.createFamilyMember(
                    token = token,
                    nombre = nombre,
                    fechaNacimiento = fechaNacimiento,
                    parentesco = parentesco
                )

                integrantes.add(nuevoIntegrante)
                onSuccess()
            } catch (e: Exception) {
                errorIntegrantes.value =
                    e.message ?: "No se pudo registrar el integrante."
            } finally {
                guardandoIntegrante.value = false
            }
        }
    }

    fun actualizarIntegrante(
        id: String,
        nombre: String,
        fechaNacimiento: String,
        parentesco: String,
        onSuccess: () -> Unit = {}
    ) {
        val token = accessToken.value

        if (token.isNullOrBlank()) {
            errorIntegrantes.value = "Tu sesión ha expirado. Inicia sesión nuevamente."
            return
        }

        if (guardandoIntegrante.value) return

        viewModelScope.launch {
            guardandoIntegrante.value = true
            errorIntegrantes.value = null

            try {
                val actualizado = familyMemberRepository.updateFamilyMember(
                    token = token,
                    id = id,
                    nombre = nombre,
                    fechaNacimiento = fechaNacimiento,
                    parentesco = parentesco
                )

                val index = integrantes.indexOfFirst {
                    it.id == actualizado.id
                }

                if (index != -1) {
                    integrantes[index] = actualizado
                }

                onSuccess()
            } catch (e: Exception) {
                errorIntegrantes.value =
                    e.message ?: "No se pudieron guardar los cambios."
            } finally {
                guardandoIntegrante.value = false
            }
        }
    }

    fun eliminarIntegrante(id: String) {
        val token = accessToken.value

        if (token.isNullOrBlank()) {
            errorIntegrantes.value = "Inicia sesión para eliminar un integrante."
            return
        }

        viewModelScope.launch {
            errorIntegrantes.value = null

            try {
                familyMemberRepository.deleteFamilyMember(
                    token = token,
                    id = id
                )

                integrantes.removeAll { it.id == id }
            } catch (e: Exception) {
                errorIntegrantes.value =
                    e.message ?: "No se pudo eliminar el integrante."
            }
        }
    }


    fun limpiarErrorIntegrantes() {
        errorIntegrantes.value = null
    }
}