package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.MessageCreateRequest
import mx.tec.familias.data.remote.MessageResponse
import mx.tec.familias.data.remote.MessageUserResponse


class MessageRepository {

    suspend fun getMessageUsers(
        token: String
    ): List<MessageUserResponse> {
        return ApiClient.apiService.getMessageUsers(
            authorization = "Bearer $token"
        )
    }

    suspend fun getMessages(
        token: String,
        userId: Int
    ): List<MessageResponse> {
        return ApiClient.apiService.getMessages(
            authorization = "Bearer $token",
            userId = userId
        )
    }

    suspend fun getActivityMessages(
        token: String,
        activityId: Int,
        userId: Int
    ): List<MessageResponse> {
        return ApiClient.apiService.getActivityMessages(
            authorization = "Bearer $token",
            activityId = activityId,
            userId = userId
        )
    }

    suspend fun getEventMessages(
        token: String,
        eventId: Int,
        userId: Int
    ): List<MessageResponse> {
        return ApiClient.apiService.getEventMessages(
            authorization = "Bearer $token",
            eventId = eventId,
            userId = userId
        )
    }

    suspend fun getWorkshopMessages(
        token: String,
        workshopId: Int,
        userId: Int
    ): List<MessageResponse> {
        return ApiClient.apiService.getWorkshopMessages(
            authorization = "Bearer $token",
            workshopId = workshopId,
            userId = userId
        )
    }

    suspend fun sendMessage(
        token: String,
        receiverUserId: Int,
        text: String,
        activityId: Int? = null,
        eventId: Int? = null,
        workshopId: Int? = null
    ): MessageResponse {
        return ApiClient.apiService.sendMessage(
            authorization = "Bearer $token",
            request = MessageCreateRequest(
                receiver_user_id = receiverUserId,
                activity_id = activityId,
                event_id = eventId,
                workshop_id = workshopId,
                text = text
            )
        )
    }
}