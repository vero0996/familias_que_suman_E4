package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.CurrentUserResponse
import mx.tec.familias.data.remote.LoginRequest
import mx.tec.familias.data.remote.RegisterRequest
import mx.tec.familias.data.remote.TokenResponse
import mx.tec.familias.data.remote.ForgotPasswordRequest
import mx.tec.familias.data.remote.RecoveryResponse

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
                email = email.trim().lowercase(),
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
                email = email.trim().lowercase(),
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

    suspend fun forgotPassword(email: String): RecoveryResponse {
        return ApiClient.apiService.forgotPassword(
            ForgotPasswordRequest(
                email = email.trim()
            )
        )
    }
}
