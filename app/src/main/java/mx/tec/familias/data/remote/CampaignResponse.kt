package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CampaignResponse(
    val id: Int,
    val title: String,
    val description: String,
    val organization_name: String,
    val status: String,
    val created_by_user_id: Int? = null,
    val created_at: String,
    val updated_at: String
)

@Serializable
data class CampaignUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val organization_name: String? = null,
    val status: String? = null
)
