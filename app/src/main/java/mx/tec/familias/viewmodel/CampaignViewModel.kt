package mx.tec.familias.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.CampaignUpdateRequest
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.repository.CampaignRepository

data class CampaignUiState(
    val campaigns: List<CampaignResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CampaignViewModel(
    private val sessionManager: SessionManager? = null
) : ViewModel() {

    private val repository = CampaignRepository()

    private val _uiState = MutableStateFlow(CampaignUiState())
    val uiState: StateFlow<CampaignUiState> = _uiState.asStateFlow()

    init {
        loadCampaigns()
    }

    fun loadCampaigns() {
        viewModelScope.launch {
            _uiState.value = CampaignUiState(isLoading = true)

            try {
                val campaigns = repository.getCampaigns()
                _uiState.value = CampaignUiState(
                    campaigns = campaigns
                )
            } catch (e: Exception) {
                _uiState.value = CampaignUiState(
                    error = e.message ?: "No se pudieron cargar las campañas."
                )
            }
        }
    }



    fun updateCampaign(
        campaignId: Int,
        request: CampaignUpdateRequest,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val token = sessionManager?.getToken()
                    ?.takeIf { it.isNotBlank() }
                    ?: throw IllegalStateException(
                        "Tu sesión ha expirado. Inicia sesión nuevamente."
                    )

                repository.updateCampaign(token, campaignId, request)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )

                onSuccess()

            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e(
                    "CampaignUpdate",
                    "Error al actualizar campaña $campaignId",
                    e
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                        ?: "No se pudieron guardar los cambios."
                )
            }
        }
    }
}
