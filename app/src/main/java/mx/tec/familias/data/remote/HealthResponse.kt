package mx.tec.familias.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val database: String? = null
)