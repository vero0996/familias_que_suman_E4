package mx.tec.familias

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import mx.tec.familias.navigation.AppNavigation
import mx.tec.familias.notifications.NotificationWorker
import mx.tec.familias.ui.theme.FamiliasQueSumanTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        val immediateWorkRequest =
            OneTimeWorkRequestBuilder<NotificationWorker>()
                .build()

        WorkManager.getInstance(this).enqueue(
            immediateWorkRequest
        )

        val periodicWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(
                15,
                TimeUnit.MINUTES
            ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notification_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )

        enableEdgeToEdge()

        setContent {
            FamiliasQueSumanTheme {
                AppNavigation()
            }
        }
    }
}