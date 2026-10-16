package mx.tec.familias.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {

    // URL de tu túnel de Cloudflare (o localhost para emulador: "http://10.0.2.2:8000/")
    // IMPORTANTE: Retrofit exige que la URL base termine obligatoriamente con barra diagonal (/)
    // y no debe incluir "/docs" al final, ya que los endpoints están en la raíz.
    private const val BASE_URL = "https://places-road-climate-exhibits.trycloudflare.com/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(ApiService::class.java)
    }
}
