package mx.tec.familias.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FamilyMemberResponse(
    val id: Int,
    @SerialName("family_id")
    val familyId: Int,
    @SerialName("full_name")
    val fullName: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String? = null,
    val age: Int? = null,
    @SerialName("relationship_type")
    val relationshipType: String,
    @SerialName("is_active")
    val isActive: Boolean = true
)

@Serializable
data class FamilyMemberCreateRequest(
    @SerialName("full_name")
    val fullName: String,
    @SerialName("date_of_birth")
    val dateOfBirth: String? = null,
    val age: Int? = null,
    @SerialName("relationship_type")
    val relationshipType: String
)