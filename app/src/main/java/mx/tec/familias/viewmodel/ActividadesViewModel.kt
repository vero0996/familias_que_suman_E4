package mx.tec.familias.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import mx.tec.familias.data.repository.ActividadesRepository

class ActividadesViewModel : ViewModel() {
    private val repositorio = ActividadesRepository.prototipo()
    var estado by mutableStateOf(repositorio.consultar())
        private set
    var eventoSeleccionadoId by mutableStateOf("arboles")
        private set
    var ultimaInscripcionId by mutableStateOf<String?>(null)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    fun seleccionarEvento(id: String) {
        estado.evento(id)
        eventoSeleccionadoId = id
        error = null
    }

    fun inscribir(familiaId: String, participantes: List<String>, observaciones: String): Boolean {
        error = null
        return try {
            ultimaInscripcionId = repositorio.inscribir(
                eventoSeleccionadoId, familiaId, participantes, observaciones
            ).id
            estado = repositorio.consultar()
            true
        } catch (e: IllegalArgumentException) {
            error = e.message ?: "No se pudo completar la solicitud."
            false
        }
    }
}
