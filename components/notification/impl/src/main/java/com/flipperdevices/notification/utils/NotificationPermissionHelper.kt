package com.flipperdevices.notification.utils

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.flipperdevices.core.permission.api.PermissionRequestHandler
import com.flipperdevices.notification.model.NotificationPermissionState
import kotlinx.coroutines.CompletableDeferred
import javax.inject.Inject

class NotificationPermissionHelper @Inject constructor(
    private val context: Context,
    private val permissionRequestHandler: PermissionRequestHandler
) {
    suspend fun requestPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        val result = CompletableDeferred<Boolean>()
        permissionRequestHandler.requestPermission(Manifest.permission.POST_NOTIFICATIONS) { _, isGranted ->
            result.complete(isGranted)
        }
        return result.await()
    }

    fun isPermissionGranted(channelId: String): NotificationPermissionState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return NotificationPermissionState.DISABLED
            }
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        val channel = manager?.getNotificationChannel(channelId)
            ?: return NotificationPermissionState.DISABLED_CHANNEL
        if (channel.importance == NotificationManager.IMPORTANCE_NONE) {
            return NotificationPermissionState.DISABLED_CHANNEL
        }
        return NotificationPermissionState.GRANTED
    }
}
