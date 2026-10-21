package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(
    val id: Int,
    val title: String,
    val description: String,
    val created_at: String
)

@Serializable
data class ProjectUpdateRequest(
    val title: String,
    val description: String
)

@Serializable
data class EventResponse(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val starts_at: String,
    val created_at: String
)

@Serializable
data class EventUpdateRequest(
    val title: String,
    val description: String,
    val location: String,
    val starts_at: String
)

@Serializable
data class WorkshopResponse(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val starts_at: String,
    val created_at: String
)

@Serializable
data class WorkshopUpdateRequest(
    val title: String,
    val description: String,
    val location: String,
    val starts_at: String
)

@Serializable
data class DonationResponse(
    val id: Int,
    val title: String,
    val description: String,
    val goal_amount: Int,
    val current_amount: Int,
    val created_at: String
)

@Serializable
data class DonationUpdateRequest(
    val title: String,
    val description: String,
    val goal_amount: Int,
    val current_amount: Int
)

@Serializable
data class ActivityUpdateRequest(
    val title: String,
    val description: String,
    val starts_at: String,
    val ends_at: String? = null,
    val location: String,
    val capacity_people: Int,
    val registration_deadline: String? = null,
    val photo_url: String? = null,
    val status: String? = null
)
