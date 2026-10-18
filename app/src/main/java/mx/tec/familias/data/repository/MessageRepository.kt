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

    suspend fun sendMessage(
        token: String,
        receiverUserId: Int,
        text: String
    ): MessageResponse {
        return ApiClient.apiService.sendMessage(
            authorization = "Bearer $token",
            request = MessageCreateRequest(
                receiver_user_id = receiverUserId,
                text = text
            )
        )
    }
}