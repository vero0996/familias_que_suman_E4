package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ActivityResponse(
    val id: Int,
    val campaign_id: Int?,
    val title: String,
    val description: String,
    val starts_at: String,
    val ends_at: String?,
    val location: String,
    val capacity_people: Int,
    val registration_deadline: String?,
    val status: String,
    val created_by_user_id: Int?,
    val created_at: String,
    val confirmed_people: Int = 0
)