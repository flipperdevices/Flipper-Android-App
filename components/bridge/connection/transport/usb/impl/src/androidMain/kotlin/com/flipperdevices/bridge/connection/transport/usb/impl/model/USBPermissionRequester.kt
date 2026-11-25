package com.flipperdevices.bridge.connection.transport.usb.impl.model

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.core.content.ContextCompat
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.seconds

private const val ACTION_USB_PERMISSION = "com.flipperdevices.bridge.connection.USB_PERMISSION"
private val PERMISSION_TIMEOUT = 30.seconds

/**
 * Shows the system USB permission dialog and suspends until the user
 * responds or [PERMISSION_TIMEOUT] expires.
 *
 * The response broadcast is only a wake-up signal; [UsbManager.hasPermission]
 * is the source of truth, so a malformed broadcast can never produce
 * a false "not granted" result.
 */
class USBPermissionRequester(
    private val context: Context,
    private val usbManager: UsbManager
) : LogTagProvider {
    override val TAG = "USBPermissionRequester"

    private fun registerPermissionReceiver(receiver: USBPermissionReceiver) {
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(ACTION_USB_PERMISSION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun buildPermissionPendingIntent(): PendingIntent {
        val intent = Intent(ACTION_USB_PERMISSION)
        intent.setPackage(context.packageName)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private suspend fun awaitPermissionResponse(device: UsbDevice) {
        val responseArrived = CompletableDeferred<Unit>()
        val receiver = USBPermissionReceiver { responseArrived.complete(Unit) }
        registerPermissionReceiver(receiver)
        try {
            info { "Requesting USB permission" }
            usbManager.requestPermission(device, buildPermissionPendingIntent())
            val response = withTimeoutOrNull(PERMISSION_TIMEOUT) { responseArrived.await() }
            if (response == null) {
                info { "USB permission request timed out" }
            }
        } finally {
            context.unregisterReceiver(receiver)
        }
    }

    suspend fun ensurePermission(device: UsbDevice): Result<Unit> {
        if (usbManager.hasPermission(device)) {
            return Result.success(Unit)
        }
        info { "No USB permission, showing system dialog" }
        awaitPermissionResponse(device)
        return if (usbManager.hasPermission(device)) {
            info { "USB permission granted" }
            Result.success(Unit)
        } else {
            info { "USB permission was not granted" }
            Result.failure(
                USBDeviceAccessDeniedException(
                    "USB permission was not granted for device ${device.deviceName}"
                )
            )
        }
    }
}
