package com.flipperdevices.inappnotification.api.model

private const val NOTIFICATION_UPDATE_MS = 5000L
private const val NOTIFICATION_REPORT_APP_MS = 3000L
private const val NOTIFICATION_HIDE_APP_MS = 5000L

sealed class InAppNotification {
    abstract val durationMs: Long

    data class SavedKey(
        val title: String,
        override val durationMs: Long
    ) : InAppNotification()

    data object ReportApp : InAppNotification() {
        override val durationMs = NOTIFICATION_REPORT_APP_MS
    }

    data class SelfUpdateReady(
        val action: () -> Unit,
        override val durationMs: Long = NOTIFICATION_UPDATE_MS
    ) : InAppNotification()

    data class SelfUpdateStarted(
        override val durationMs: Long = NOTIFICATION_UPDATE_MS
    ) : InAppNotification()

    data class SelfUpdateError(
        override val durationMs: Long = NOTIFICATION_UPDATE_MS
    ) : InAppNotification()

    data class HiddenApp(
        val action: () -> Unit,
        override val durationMs: Long = NOTIFICATION_HIDE_APP_MS
    ) : InAppNotification()
}
