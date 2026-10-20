package mx.tec.familias.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import mx.tec.familias.data.remote.SessionManager
import mx.tec.familias.data.repository.NotificationRepository

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val sessionManager = SessionManager(applicationContext)
            val token = sessionManager.getToken()

            if (token == null) {
                return Result.success()
            }

            val repository = NotificationRepository()
            val notifications = repository.getNotifications(token)

            notifications
                .filter { !it.is_read }
                .forEach { notification ->

                    NotificationHelper.showNotification(
                        context = applicationContext,
                        id = notification.id,
                        title = notification.title,
                        message = notification.message
                    )

                    repository.readNotification(
                        token = token,
                        notificationId = notification.id
                    )
                }

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}