package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val full_name: String,
    val email: String,
    val phone: String,
    val password: String,
    val family_name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class TokenResponse(
    val access_token: String,
    val token_type: String = "bearer"
)
