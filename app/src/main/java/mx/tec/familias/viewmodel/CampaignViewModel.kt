package mx.tec.familias.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.tec.familias.data.remote.CampaignCreateRequest
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
                val campaigns = if (sessionManager == null) {
                    repository.getCampaigns()
                } else {
                    val token = sessionManager.getToken()
                        ?.takeIf(String::isNotBlank)
                        ?: throw IllegalStateException(
                            "Tu sesión ha expirado. Inicia sesión nuevamente."
                        )
                    repository.getManagedCampaigns(token)
                }
                _uiState.value = CampaignUiState(
                    campaigns = campaigns
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = CampaignUiState(
                    error = e.message ?: "No se pudieron cargar las campañas."
                )
            }
        }
    }

    fun createCampaign(
        context: Context,
        title: String,
        description: String,
        organizationName: String,
        photoUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val token = sessionManager?.getToken()
                    ?.takeIf(String::isNotBlank)
                    ?: throw IllegalStateException(
                        "Tu sesión ha expirado. Inicia sesión nuevamente."
                    )
                val photoUrl = photoUri?.let {
                    repository.uploadPhoto(context, token, it).url
                }
                val created = repository.createCampaign(
                    token,
                    CampaignCreateRequest(
                        title = title.trim(),
                        description = description.trim(),
                        organization_name = organizationName.trim(),
                        photo_url = photoUrl
                    )
                )
                _uiState.value = _uiState.value.copy(
                    campaigns = listOf(created) + _uiState.value.campaigns
                        .filterNot { it.id == created.id },
                    isLoading = false,
                    error = null
                )
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("CampaignCreate", "Error al crear campaña", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "No se pudo crear la campaña."
                )
            }
        }
    }


    fun updateCampaign(
        context: Context,
        campaignId: Int,
        request: CampaignUpdateRequest,
        photoUri: Uri?,
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

                val photoUrl = photoUri?.let {
                    repository.uploadPhoto(context, token, it).url
                }
                repository.updateCampaign(
                    token,
                    campaignId,
                    request.copy(photo_url = photoUrl ?: request.photo_url)
                )

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

                val errorMessage = if (e is retrofit2.HttpException) {
                    val errorDetail = try {
                        val errorJson = e.response()?.errorBody()?.string()
                        if (!errorJson.isNullOrBlank()) {
                            org.json.JSONObject(errorJson)
                                .optString("detail")
                                .takeIf { it.isNotBlank() }
                        } else {
                            null
                        }
                    } catch (_: Exception) {
                        null
                    }

                    errorDetail
                        ?: "No se pudieron guardar los cambios (Error ${e.code()})."
                } else {
                    e.message ?: "No se pudieron guardar los cambios."
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }
}
