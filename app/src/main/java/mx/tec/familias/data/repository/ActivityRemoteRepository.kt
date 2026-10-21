package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ActivityResponse
import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.ManagedActivityRegistrationResponse

class ActivityRemoteRepository {

    suspend fun getActivities(): List<ActivityResponse> =
        ApiClient.apiService.getActivities()

    suspend fun getManagedActivities(token: String): List<ActivityResponse> =
        ApiClient.apiService.getManagedActivities(authorizationHeader(token))

    suspend fun getActivity(id: Int): ActivityResponse =
        ApiClient.apiService.getActivity(id)

    suspend fun getManagedRegistrations(
        token: String,
        activityId: Int
    ): List<ManagedActivityRegistrationResponse> =
        ApiClient.apiService.getManagedActivityRegistrations(
            authorizationHeader(token),
            activityId
        )

    suspend fun cancelRegistration(
        token: String,
        activityId: Int,
        registrationId: Int
    ): List<ManagedActivityRegistrationResponse> =
        ApiClient.apiService.cancelManagedActivityRegistration(
            authorizationHeader(token),
            activityId,
            registrationId
        )

    suspend fun acceptRegistration(
        token: String,
        activityId: Int,
        registrationId: Int
    ): List<ManagedActivityRegistrationResponse> =
        ApiClient.apiService.acceptManagedActivityRegistration(
            authorizationHeader(token),
            activityId,
            registrationId
        )

    suspend fun cancelActivity(token: String, activityId: Int) {
        ApiClient.apiService.cancelActivity(authorizationHeader(token), activityId)
    }

    private fun authorizationHeader(token: String): String =
        "Bearer " + token.trim()
}
