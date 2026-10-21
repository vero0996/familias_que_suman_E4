package mx.tec.familias.data.repository

import mx.tec.familias.data.remote.ApiClient
import mx.tec.familias.data.remote.NotificationResponse

class NotificationRepository {

    suspend fun getNotifications(
        token: String
    ): List<NotificationResponse> {
        return ApiClient.apiService.getNotifications(
            authorization = "Bearer $token"
        )
    }

    suspend fun readNotification(
        token: String,
        notificationId: Int
    ): NotificationResponse {
        return ApiClient.apiService.readNotification(
            authorization = "Bearer $token",
            notificationId = notificationId
        )
    }
}