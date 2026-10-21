package mx.tec.familias.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Taller(
    val id: Int,
    val title: String,
    val description: String,
    val location: String,
    val starts_at: String,
    val created_at: String
)