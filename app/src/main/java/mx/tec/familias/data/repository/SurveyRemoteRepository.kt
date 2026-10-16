
package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.SurveyCreateRequest
import mx.tec.familias.data.remote.SurveyResponse

class SurveyRemoteRepository {

    suspend fun getMySurveys(
        token: String
    ): List<SurveyResponse> {
        return ApiClient.apiService.getMySurveys(
            authorization = "Bearer $token"
        )
    }

    suspend fun createSurvey(
        token: String,
        request: SurveyCreateRequest
    ): SurveyResponse {
        return ApiClient.apiService.createSurvey(
            authorization = "Bearer $token",
            request = request
        )
    }
}
