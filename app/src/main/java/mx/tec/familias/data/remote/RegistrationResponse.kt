package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationResponse(
    val id: Int,
    val activity_id: Int,
    val family_id: Int,
    val status: String,
    val notes: String? = null,
    val created_at: String,
    val updated_at: String,
    val canceled_at: String? = null
)

