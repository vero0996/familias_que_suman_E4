package mx.tec.familias.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.repository.CampaignRepository

data class CampaignUiState(
    val campaigns: List<CampaignResponse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class CampaignViewModel : ViewModel() {

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
}