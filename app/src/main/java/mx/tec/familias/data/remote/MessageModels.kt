package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable


@Serializable
data class MessageCreateRequest(
    val receiver_user_id: Int,
    val activity_id: Int? = null,
    val event_id: Int? = null,
    val workshop_id: Int? = null,
    val text: String
)


@Serializable
data class MessageResponse(
    val id: Int,
    val sender_user_id: Int,
    val receiver_user_id: Int,
    val activity_id: Int? = null,
    val event_id: Int? = null,
    val workshop_id: Int? = null,
    val text: String,
    val created_at: String
)


@Serializable
data class MessageUserResponse(
    val id: Int,
    val full_name: String,
    val role: String
)