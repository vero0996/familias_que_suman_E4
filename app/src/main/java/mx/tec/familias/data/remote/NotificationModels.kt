package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Int,
    val user_id: Int,
    val title: String,
    val message: String,
    val notification_type: String,
    val is_read: Boolean,
    val created_at: String
)