package com.flipperdevices.inappnotification.api.model

private const val NOTIFICATION_UPDATE_MS = 5000L
private const val NOTIFICATION_REPORT_APP_MS = 3000L

sealed class InAppNotification(val durationMs: Long) {
    class SavedKey(
        val title: String,
        durationMs: Long
    ) : InAppNotification(durationMs)

    object ReportApp : InAppNotification(NOTIFICATION_REPORT_APP_MS)

    class UpdateReady(
        val action: () -> Unit,
        durationMs: Long = NOTIFICATION_UPDATE_MS
    ) : InAppNotification(durationMs)
}
