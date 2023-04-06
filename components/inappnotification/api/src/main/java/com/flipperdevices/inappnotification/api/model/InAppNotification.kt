package com.flipperdevices.inappnotification.api.model

private const val NOTIFICATION_UPDATE_MS = 5000L

sealed class InAppNotification(val durationMs: Long) {
    class SavedKey(
        val title: String,
        durationMs: Long
    ) : InAppNotification(durationMs)

    class UpdateReady(
        val action: () -> Unit,
        durationMs: Long = NOTIFICATION_UPDATE_MS
    ) : InAppNotification(durationMs)
}

