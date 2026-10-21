package mx.tec.familias.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.tec.familias.data.remote.ActivityUpdateRequest
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.CampaignUpdateRequest
import mx.tec.familias.data.remote.DonationUpdateRequest
import mx.tec.familias.data.remote.EventUpdateRequest
import mx.tec.familias.data.remote.ProjectUpdateRequest
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.remote.WorkshopUpdateRequest
import mx.tec.familias.data.repository.ActivityRemoteRepository
import mx.tec.familias.data.repository.AdminPublicationsRepository
import mx.tec.familias.data.repository.CampaignRepository
import retrofit2.HttpException

enum class PublicationKind {
    CAMPAIGN,
    ACTIVITY,
    PROJECT,
    EVENT,
    WORKSHOP,
    DONATION
}

data class EditablePublication(
    val kind: PublicationKind,
    val id: Int,
    val title: String,
    val description: String,
    val organizationName: String = "",
    val location: String = "",
    val startsAt: String = "",
    val endsAt: String = "",
    val registrationDeadline: String = "",
    val capacityPeople: String = "",
    val goalAmount: String = "",
    val currentAmount: String = ""
)

data class AdminPublicationsUiState(
    val campaigns: List<EditablePublication> = emptyList(),
    val activities: List<EditablePublication> = emptyList(),
    val projects: List<EditablePublication> = emptyList(),
    val events: List<EditablePublication> = emptyList(),
    val workshops: List<EditablePublication> = emptyList(),
    val donations: List<EditablePublication> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun find(kind: PublicationKind, id: Int): EditablePublication? =
        listFor(kind).find { it.id == id }

    private fun listFor(kind: PublicationKind): List<EditablePublication> =
        when (kind) {
            PublicationKind.CAMPAIGN -> campaigns
            PublicationKind.ACTIVITY -> activities
            PublicationKind.PROJECT -> projects
            PublicationKind.EVENT -> events
            PublicationKind.WORKSHOP -> workshops
            PublicationKind.DONATION -> donations
        }
}

class AdminPublicationsViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {
    private val campaignRepository = CampaignRepository()
    private val publicationRepository = AdminPublicationsRepository()
    private val activityRepository = ActivityRemoteRepository()

    private val _uiState = MutableStateFlow(AdminPublicationsUiState())
    val uiState: StateFlow<AdminPublicationsUiState> = _uiState.asStateFlow()

    fun loadPublications() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val loaded = coroutineScope {
                    val campaigns = async { campaignRepository.getCampaigns() }
                    val activities = async { activityRepository.getActivities() }
                    val projects = async { publicationRepository.getProjects() }
                    val events = async { publicationRepository.getEvents() }
                    val workshops = async { publicationRepository.getWorkshops() }
                    val donations = async { publicationRepository.getDonations() }
                    AdminPublicationsUiState(
                        campaigns = campaigns.await().map { it.toEditable() },
                        activities = activities.await().map { it.toEditable() },
                        projects = projects.await().map { it.toEditable() },
                        events = events.await().map { it.toEditable() },
                        workshops = workshops.await().map { it.toEditable() },
                        donations = donations.await().map { it.toEditable() }
                    )
                }
                _uiState.value = loaded
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AdminPublications", "Error al cargar publicaciones", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.userMessage("No se pudieron cargar las publicaciones.")
                )
            }
        }
    }

    fun updatePublication(
        draft: EditablePublication,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val token = sessionManager.getToken()
                    ?.takeIf(String::isNotBlank)
                    ?: throw IllegalStateException(
                        "Tu sesión ha expirado. Inicia sesión nuevamente."
                    )

                val updated = when (draft.kind) {
                    PublicationKind.CAMPAIGN -> {
                        val request = CampaignUpdateRequest(
                            title = draft.title,
                            description = draft.description,
                            organization_name = draft.organizationName,
                            status = null
                        )
                        campaignRepository.updateCampaign(token, draft.id, request)
                            .toEditable()
                    }
                    PublicationKind.ACTIVITY -> {
                        val capacity = draft.capacityPeople.toIntOrNull()
                            ?: throw IllegalArgumentException("La capacidad debe ser un número entero.")
                        publicationRepository.updateActivity(
                            token,
                            draft.id,
                            ActivityUpdateRequest(
                                title = draft.title,
                                description = draft.description,
                                starts_at = draft.startsAt,
                                ends_at = draft.endsAt.ifBlank { null },
                                location = draft.location,
                                capacity_people = capacity,
                                registration_deadline =
                                    draft.registrationDeadline.ifBlank { null }
                            )
                        ).toEditable()
                    }
                    PublicationKind.PROJECT -> publicationRepository.updateProject(
                        token,
                        draft.id,
                        ProjectUpdateRequest(draft.title, draft.description)
                    ).toEditable()
                    PublicationKind.EVENT -> publicationRepository.updateEvent(
                        token,
                        draft.id,
                        EventUpdateRequest(
                            draft.title,
                            draft.description,
                            draft.location,
                            draft.startsAt
                        )
                    ).toEditable()
                    PublicationKind.WORKSHOP -> publicationRepository.updateWorkshop(
                        token,
                        draft.id,
                        WorkshopUpdateRequest(
                            draft.title,
                            draft.description,
                            draft.location,
                            draft.startsAt
                        )
                    ).toEditable()
                    PublicationKind.DONATION -> {
                        val goal = draft.goalAmount.toIntOrNull()
                            ?: throw IllegalArgumentException("La meta debe ser un número entero.")
                        val current = draft.currentAmount.toIntOrNull()
                            ?: throw IllegalArgumentException("El monto actual debe ser un número entero.")
                        publicationRepository.updateDonation(
                            token,
                            draft.id,
                            DonationUpdateRequest(
                                draft.title,
                                draft.description,
                                goal,
                                current
                            )
                        ).toEditable()
                    }
                }

                _uiState.value = _uiState.value.replace(updated).copy(
                    isLoading = false,
                    error = null
                )
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(
                    "AdminPublications",
                    "Error al actualizar ${draft.kind} ${draft.id}",
                    e
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.userMessage("No se pudieron guardar los cambios.")
                )
            }
        }
    }

    private fun AdminPublicationsUiState.replace(
        publication: EditablePublication
    ): AdminPublicationsUiState {
        fun replace(items: List<EditablePublication>) =
            items.map { if (it.id == publication.id) publication else it }

        return when (publication.kind) {
            PublicationKind.CAMPAIGN -> copy(campaigns = replace(campaigns))
            PublicationKind.ACTIVITY -> copy(activities = replace(activities))
            PublicationKind.PROJECT -> copy(projects = replace(projects))
            PublicationKind.EVENT -> copy(events = replace(events))
            PublicationKind.WORKSHOP -> copy(workshops = replace(workshops))
            PublicationKind.DONATION -> copy(donations = replace(donations))
        }
    }

    private fun Exception.userMessage(fallback: String): String {
        if (this !is HttpException) return message ?: fallback
        val body = response()?.errorBody()?.string()
        return try {
            org.json.JSONObject(body.orEmpty())
                .optString("detail")
                .takeIf(String::isNotBlank)
                ?: "No se pudieron guardar los cambios (Error ${code()})."
        } catch (e: org.json.JSONException) {
            Log.w("AdminPublications", "La respuesta de error no contiene JSON válido", e)
            "No se pudieron guardar los cambios (Error ${code()})."
        }
    }

    private fun CampaignResponse.toEditable() = EditablePublication(
        kind = PublicationKind.CAMPAIGN,
        id = id,
        title = title,
        description = description,
        organizationName = organization_name
    )

    private fun mx.tec.familias.data.remote.ActivityResponse.toEditable() =
        EditablePublication(
            kind = PublicationKind.ACTIVITY,
            id = id,
            title = title,
            description = description,
            location = location,
            startsAt = starts_at,
            endsAt = ends_at.orEmpty(),
            registrationDeadline = registration_deadline.orEmpty(),
            capacityPeople = capacity_people.toString()
        )

    private fun mx.tec.familias.data.remote.ProjectResponse.toEditable() =
        EditablePublication(PublicationKind.PROJECT, id, title, description)

    private fun mx.tec.familias.data.remote.EventResponse.toEditable() =
        EditablePublication(
            PublicationKind.EVENT,
            id,
            title,
            description,
            location = location,
            startsAt = starts_at
        )

    private fun mx.tec.familias.data.remote.WorkshopResponse.toEditable() =
        EditablePublication(
            PublicationKind.WORKSHOP,
            id,
            title,
            description,
            location = location,
            startsAt = starts_at
        )

    private fun mx.tec.familias.data.remote.DonationResponse.toEditable() =
        EditablePublication(
            PublicationKind.DONATION,
            id,
            title,
            description,
            goalAmount = goal_amount.toString(),
            currentAmount = current_amount.toString()
        )

    class Factory(
        private val sessionManager: SessionManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (!modelClass.isAssignableFrom(AdminPublicationsViewModel::class.java)) {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
            return AdminPublicationsViewModel(sessionManager) as T
        }
    }
}
