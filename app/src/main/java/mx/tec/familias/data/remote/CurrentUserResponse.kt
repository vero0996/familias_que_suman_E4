
package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CurrentUserResponse(
    val id: Int,
    val full_name: String,
    val email: String,
    val phone: String? = null,
    val role: String
)
