package mx.tec.familias.data.remote

import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object ApiClient {

    // CAMBIAR URL cada que se levante el backend
    private const val BASE_URL =
        "https://incentive-duncan-result-oils.trycloudflare.com/"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // Cambiar el pin cada que cambie el dominio
    private val certificatePinner = CertificatePinner.Builder()
        .add(
            "*.trycloudflare.com",
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