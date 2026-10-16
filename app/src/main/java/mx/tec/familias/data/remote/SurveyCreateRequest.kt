
package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class SurveyCreateRequest(
    val registration_id: Int,
    val campaign_rating: Int,
    val organization_rating: Int,
    val communication_rating: Int,
    val overall_rating: Int,
    val comment: String? = null,
    val photo_url: String? = null,
    val photo_consent: Boolean = false
)
