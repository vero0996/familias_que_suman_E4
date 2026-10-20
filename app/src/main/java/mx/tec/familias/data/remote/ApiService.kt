package mx.tec.familias.data.remote

import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.PATCH

interface ApiService {

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): CurrentUserResponse

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

    @GET("activities")
    suspend fun getActivities(): List<ActivityResponse>

    @GET("activities/{activity_id}")
    suspend fun getActivity(
        @Path("activity_id") activityId: Int
    ): ActivityResponse

    @POST("registrations")
    suspend fun createRegistration(
        @Header("Authorization") authorization: String,
        @Body request: RegistrationRequest
    ): RegistrationResponse

    @GET("family-members/me")
    suspend fun getFamilyMembers(
        @Header("Authorization") authorization: String
    ): List<FamilyMemberResponse>

    @POST("family-members")
    suspend fun createFamilyMember(
        @Header("Authorization") authorization: String,
        @Body request: FamilyMemberCreateRequest
    ): FamilyMemberResponse

    @DELETE("family-members/{member_id}")
    suspend fun deleteFamilyMember(
        @Header("Authorization") authorization: String,
        @Path("member_id") memberId: Int
    )

    @PUT("family-members/{member_id}")
    suspend fun updateFamilyMember(
        @Header("Authorization") authorization: String,
        @Path("member_id") memberId: Int,
        @Body request: FamilyMemberCreateRequest
    ): FamilyMemberResponse

    @GET("registrations/me")
    suspend fun getMyRegistrations(
        @Header("Authorization") authorization: String
    ): List<RegistrationResponse>

    @PATCH("registrations/{registration_id}/cancel")
    suspend fun cancelRegistration(
        @Header("Authorization") authorization: String,
        @Path("registration_id") registrationId: Int,
        @Body request: RegistrationCancelRequest
    ): RegistrationResponse

    @POST("surveys")
    suspend fun createSurvey(
        @Header("Authorization") authorization: String,
        @Body request: SurveyCreateRequest
    ): SurveyResponse

    @GET("surveys/me")
    suspend fun getMySurveys(
        @Header("Authorization") authorization: String
    ): List<SurveyResponse>

    @retrofit2.http.GET("api/proyectos/")
    suspend fun getProyectos(): retrofit2.Response<List<mx.tec.familias.data.model.Proyecto>>

    @retrofit2.http.GET("api/eventos/")
    suspend fun getEventos(): retrofit2.Response<List<mx.tec.familias.data.model.Evento>>

    @retrofit2.http.GET("api/talleres/")
    suspend fun getTalleres(): retrofit2.Response<List<mx.tec.familias.data.model.Taller>>

    @retrofit2.http.Multipart
    @retrofit2.http.POST("api/fotos/")
    suspend fun uploadFoto(
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part
    ): retrofit2.Response<mx.tec.familias.data.model.UploadResponse>

    @GET("api/mensajes/usuarios")
    suspend fun getMessageUsers(
        @Header("Authorization") authorization: String
    ): List<MessageUserResponse>

    @GET("api/mensajes/{user_id}")
    suspend fun getMessages(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: Int
    ): List<MessageResponse>

    @POST("api/mensajes/")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Body request: MessageCreateRequest
    ): MessageResponse
}
