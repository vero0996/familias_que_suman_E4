package mx.tec.familias.data.remote

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface ApiService {

    @GET("health")
    suspend fun getHealth(): HealthResponse

    @GET("health/db")
    suspend fun getDatabaseHealth(): HealthResponse

    @GET("campaigns")
    suspend fun getCampaigns(): List<CampaignResponse>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): CurrentUserResponse

}