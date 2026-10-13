package mx.tec.familias.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import mx.tec.familias.data.model.FamilyMember
import mx.tec.familias.data.model.UserProfile
import java.util.UUID

class FamilyViewModel : ViewModel() {

    // Identidad local del prototipo; un backend deberá usar el ID autenticado.
    val familiaId: String
        get() = usuario.value?.correo?.trim()?.lowercase(java.util.Locale.ROOT)
            ?.let { "familia:$it" } ?: ""

    // Información de la persona que se registró
    var usuario = mutableStateOf<UserProfile?>(null)
        private set

    // Token de autenticación recibido del backend.
    var accessToken = mutableStateOf<String?>(null)
        private set

    fun guardarSesion(token: String) {
        accessToken.value = token
    }

    fun cerrarSesion() {
        accessToken.value = null
        usuario.value = null
        integrantes.clear()
    }

    // Familiares y acompañantes
    val integrantes = mutableStateListOf<FamilyMember>()

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


    fun agregarIntegrante(
        nombre: String,
        fechaNacimiento: String,
        parentesco: String
    ) {
        integrantes.add(
            FamilyMember(
                id = UUID.randomUUID().toString(),
                nombre = nombre.trim(),
                fechaNacimiento = fechaNacimiento.trim(),
                parentesco = parentesco.trim()
            )
        )
    }

    fun eliminarIntegrante(id: String) {
        integrantes.removeAll {
            it.id == id
        }
    }
}
