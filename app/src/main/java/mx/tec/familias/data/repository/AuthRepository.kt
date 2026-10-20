package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.CurrentUserResponse
import mx.tec.familias.data.remote.LoginRequest
import mx.tec.familias.data.remote.RegisterRequest
import mx.tec.familias.data.remote.TokenResponse

class AuthRepository {

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): CurrentUserResponse {
        return ApiClient.apiService.register(
            RegisterRequest(
                full_name = name.trim(),
                email = email.trim(),
                phone = phone.trim(),
                password = password,
                family_name = name.trim()
            )
        )
    }

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
