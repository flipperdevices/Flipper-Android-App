package com.flipperdevices.notification.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.flipperdevices.core.permission.api.PermissionRequestHandler
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
        val result = CompletableDeferred<Boolean>()
        permissionRequestHandler.requestPermission(Manifest.permission.POST_NOTIFICATIONS) { _, isGranted ->
            result.complete(isGranted)
        }
        return result.await()
    }

    fun isPermissionGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}
