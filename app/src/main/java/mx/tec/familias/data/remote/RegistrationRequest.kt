package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationRequest(
    val activity_id: Int,
    val participant_ids: List<Int>,
    val notes: String? = null
)

@Serializable
data class RegistrationCancelRequest(
    val reason: String? = null
)
