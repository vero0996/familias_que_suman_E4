package mx.tec.familias.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    // Familiares y acompañantes
    val integrantes = mutableStateListOf<FamilyMember>()

    // Agrega esta variable dentro de tu FamilyViewModel:
    var actividadInscrita by mutableStateOf(false)

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

    fun agregarIntegrante(
        nombre: String,
        edad: Int,
        parentesco: String
    ) {
        integrantes.add(
            FamilyMember(
                id = UUID.randomUUID().toString(),
                nombre = nombre.trim(),
                edad = edad,
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