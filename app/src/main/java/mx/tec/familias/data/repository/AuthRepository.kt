
package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.CurrentUserResponse
import mx.tec.familias.data.remote.LoginRequest
import mx.tec.familias.data.remote.TokenResponse

class AuthRepository {

    suspend fun login(
        email: String,
        password: String
    ): TokenResponse {
        return ApiClient.apiService.login(
            LoginRequest(
                email = email.trim(),
                password = password
            )
        )
    }

    suspend fun getCurrentUser(
        token: String
    ): CurrentUserResponse {
        return ApiClient.apiService.getCurrentUser(
            authorization = "Bearer $token"
        )
    }
}
