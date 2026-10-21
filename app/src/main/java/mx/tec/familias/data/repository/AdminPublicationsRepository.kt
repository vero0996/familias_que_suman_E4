package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ActivityResponse
import mx.tec.familias.data.remote.ActivityUpdateRequest
import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.DonationResponse
import mx.tec.familias.data.remote.DonationUpdateRequest
import mx.tec.familias.data.remote.EventResponse
import mx.tec.familias.data.remote.EventUpdateRequest
import mx.tec.familias.data.remote.ProjectResponse
import mx.tec.familias.data.remote.ProjectUpdateRequest
import mx.tec.familias.data.remote.WorkshopResponse
import mx.tec.familias.data.remote.WorkshopUpdateRequest

class AdminPublicationsRepository {
    suspend fun getActivities(): List<ActivityResponse> =
        ApiClient.apiService.getActivities()

    suspend fun updateActivity(
        token: String,
        id: Int,
        request: ActivityUpdateRequest
    ): ActivityResponse =
        ApiClient.apiService.updateActivity("Bearer $token", id, request)

    suspend fun getProjects(): List<ProjectResponse> =
        ApiClient.apiService.getProyectos()

    suspend fun updateProject(
        token: String,
        id: Int,
        request: ProjectUpdateRequest
    ): ProjectResponse =
        ApiClient.apiService.updateProject("Bearer $token", id, request)

    suspend fun getEvents(): List<EventResponse> =
        ApiClient.apiService.getEventos()

    suspend fun updateEvent(
        token: String,
        id: Int,
        request: EventUpdateRequest
    ): EventResponse =
        ApiClient.apiService.updateEvent("Bearer $token", id, request)

    suspend fun getWorkshops(): List<WorkshopResponse> =
        ApiClient.apiService.getTalleres()

    suspend fun updateWorkshop(
        token: String,
        id: Int,
        request: WorkshopUpdateRequest
    ): WorkshopResponse =
        ApiClient.apiService.updateWorkshop("Bearer $token", id, request)

    suspend fun getDonations(): List<DonationResponse> =
        ApiClient.apiService.getDonations()

    suspend fun updateDonation(
        token: String,
        id: Int,
        request: DonationUpdateRequest
    ): DonationResponse =
        ApiClient.apiService.updateDonation("Bearer $token", id, request)
}
