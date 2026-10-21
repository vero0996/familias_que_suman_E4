package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ActivityResponse
import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.ManagedActivityRegistrationResponse

class ActivityRemoteRepository {

    suspend fun getActivities(): List<ActivityResponse> {
        return ApiClient.apiService.getActivities()
    }

    suspend fun getManagedActivities(token: String): List<ActivityResponse> {
        return ApiClient.apiService.getManagedActivities("Bearer $token")
    }

    suspend fun getActivity(id: Int): ActivityResponse {
        return ApiClient.apiService.getActivity(id)
    }

    suspend fun getManagedRegistrations(
        token: String,
        activityId: Int
    ): List<ManagedActivityRegistrationResponse> =
        ApiClient.apiService.getManagedActivityRegistrations(
            "Bearer $token",
            activityId
        )

    suspend fun cancelRegistration(
        token: String,
        activityId: Int,
        registrationId: Int
    ): List<ManagedActivityRegistrationResponse> =
        ApiClient.apiService.cancelManagedActivityRegistration(
            "Bearer $token",
            activityId,
            registrationId
        )

    suspend fun cancelActivity(token: String, activityId: Int) {
        ApiClient.apiService.cancelActivity("Bearer $token", activityId)
    }
}