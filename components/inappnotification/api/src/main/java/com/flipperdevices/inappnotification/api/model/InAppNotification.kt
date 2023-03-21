package com.flipperdevices.inappnotification.api.model

sealed class InAppNotification(val durationMs: Long) {
    class SavedKey(
        val title: String,
        durationMs: Long
    ) : InAppNotification(durationMs)

    class UpdateReady(
        val action: () -> Unit,
        durationMs: Long
    ) : InAppNotification(durationMs)
}
