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
    val confirmed_people: Int,
    val photo_url: String? = null
)

@Serializable
data class ManagedActivityRegistrationResponse(
    val id: Int,
    val activity_id: Int,
    val family_id: Int,
    val family_name: String,
    val contact_name: String,
    val email: String,
    val phone: String? = null,
    val status: String,
    val participant_names: List<String>,
    val people_count: Int,
    val created_at: String
)