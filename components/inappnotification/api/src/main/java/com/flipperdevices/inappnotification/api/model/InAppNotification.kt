package com.flipperdevices.inappnotification.api.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

private const val NOTIFICATION_UPDATE_MS = 5000L
private const val NOTIFICATION_REPORT_APP_MS = 3000L
private const val NOTIFICATION_APP_UPDATE_MS = 10000L
private const val NOTIFICATION_HIDE_APP_MS = 5000L
private const val NOTIFICATION_DURATION_MS = 5 * 1000L

@Immutable
sealed class InAppNotification {
    abstract val durationMs: Long

    data class Successful(
        val title: String? = null,
        @StringRes val titleId: Int? = null,
        val desc: String? = null,
        @StringRes val descId: Int? = null,
        override val durationMs: Long = NOTIFICATION_DURATION_MS
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

    data class Error(
        @StringRes val titleId: Int,
        @StringRes val descId: Int,
        @StringRes val actionTextId: Int?,
        val action: (() -> Unit)?,
        override val durationMs: Long = NOTIFICATION_HIDE_APP_MS
    ) : InAppNotification()

    data object ReadyToUpdateFaps : InAppNotification() {
        override val durationMs = NOTIFICATION_APP_UPDATE_MS
    }
}
