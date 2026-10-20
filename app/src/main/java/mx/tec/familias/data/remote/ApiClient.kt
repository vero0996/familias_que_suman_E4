package mx.tec.familias.data.remote

import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {

    private const val BASE_URL = "https://congress-means-binary-idaho.trycloudflare.com/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // AÑADIDO: Certificate Pinning para ciberseguridad (MITM)
    private val certificatePinner = CertificatePinner.Builder()
        .add("congress-means-binary-idaho.trycloudflare.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        .build()

    // AÑADIDO: Inyectar el pinning en OkHttp
    private val okHttpClient = OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // AÑADIDO: decirle a Retrofit que use OkHttp seguro
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
            .create(ApiService::class.java)
    }
}