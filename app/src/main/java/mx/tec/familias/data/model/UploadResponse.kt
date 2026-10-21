package mx.tec.familias.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadResponse(
    val mensaje: String,
    val url: String
)