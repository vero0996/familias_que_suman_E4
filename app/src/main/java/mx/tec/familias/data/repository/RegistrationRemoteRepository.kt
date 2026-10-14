
package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.RegistrationCancelRequest
import mx.tec.familias.data.remote.RegistrationRequest
import mx.tec.familias.data.remote.RegistrationResponse

class RegistrationRemoteRepository {

    suspend fun getMyRegistrations(
        token: String
    ): List<RegistrationResponse> {
        return ApiClient.apiService.getMyRegistrations(
            authorization = "Bearer $token"
        )
    }

    suspend fun createRegistration(
        token: String,
        request: RegistrationRequest
    ): RegistrationResponse {
        return ApiClient.apiService.createRegistration(
            authorization = "Bearer $token",
            request = request
        )
    }

    suspend fun cancelRegistration(
        token: String,
        registrationId: Int,
        reason: String? = null
    ): RegistrationResponse {
        return ApiClient.apiService.cancelRegistration(
            authorization = "Bearer $token",
            registrationId = registrationId,
            request = RegistrationCancelRequest(reason)
        )
    }
}
