package mx.tec.familias.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.tec.familias.data.model.UploadResponse
import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.CampaignCreateRequest
import mx.tec.familias.data.remote.CampaignResponse
import mx.tec.familias.data.remote.CampaignUpdateRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class CampaignRepository {

    suspend fun getCampaigns(): List<CampaignResponse> {
        return ApiClient.apiService.getCampaigns()
    }

    suspend fun getManagedCampaigns(token: String): List<CampaignResponse> =
        ApiClient.apiService.getManagedCampaigns("Bearer $token")

    suspend fun createCampaign(
        token: String,
        request: CampaignCreateRequest
    ): CampaignResponse =
        ApiClient.apiService.createCampaign("Bearer $token", request)

    suspend fun uploadPhoto(
        context: Context,
        token: String,
        uri: Uri
    ): UploadResponse = withContext(Dispatchers.IO) {
        val contentType = context.contentResolver.getType(uri)
            ?: throw IllegalArgumentException("No se pudo determinar el formato de la imagen.")
        val extension = when (contentType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "image/webp" -> ".webp"
            else -> throw IllegalArgumentException(
                "Selecciona una imagen JPEG, PNG o WebP."
            )
        }
        val file = File.createTempFile("campaign-photo-", extension, context.cacheDir)

        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw IllegalStateException("No se pudo leer la imagen seleccionada.")
            input.use { source -> file.outputStream().use(source::copyTo) }

            val requestBody = file.asRequestBody(contentType.toMediaType())
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            val response = ApiClient.apiService.uploadFoto("Bearer $token", part)
            if (!response.isSuccessful) throw HttpException(response)
            response.body()
                ?: throw IllegalStateException("El servidor no devolvió la URL de la imagen.")
        } finally {
            file.delete()
        }
    }

    suspend fun updateCampaign(
        token: String,
        campaignId: Int,
        request: CampaignUpdateRequest
    ): CampaignResponse {
        return ApiClient.apiService.updateCampaign(
            "Bearer $token",
            campaignId,
            request
        )
    }
}
