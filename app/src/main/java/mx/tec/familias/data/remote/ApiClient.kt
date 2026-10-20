package mx.tec.familias.data.remote

import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {

    // URL de tu túnel de Cloudflare (o localhost para emulador: "http://10.0.2.2:8000/")
    // IMPORTANTE: Retrofit exige que la URL base termine obligatoriamente con barra diagonal (/)
    // y no debe incluir "/docs" al final, ya que los endpoints están en la raíz.
    private const val BASE_URL =
        "https://nirvana-flexibility-dans-acne.trycloudflare.com/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // Cambiar el pin cada que cambie el dominio
    private val certificatePinner = CertificatePinner.Builder()
        .add(
            "nirvana-flexibility-dans-acne.trycloudflare.com",
            "sha256/uXsPjVwaDxO4vIxFBYMc1wgPXF0VN/KExd/HrvBoDwY="
        )
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(ApiService::class.java)
    }
}