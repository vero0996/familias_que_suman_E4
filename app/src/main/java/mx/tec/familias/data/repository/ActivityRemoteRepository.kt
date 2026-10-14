package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ActivityResponse
import mx.tec.familias.data.remote.ApiClient

class ActivityRemoteRepository {

    suspend fun getActivities(): List<ActivityResponse> {
        return ApiClient.apiService.getActivities()
    }

    suspend fun getActivity(id: Int): ActivityResponse {
        return ApiClient.apiService.getActivity(id)
    }
}