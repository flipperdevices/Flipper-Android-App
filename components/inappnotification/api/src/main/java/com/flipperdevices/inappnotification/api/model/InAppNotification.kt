package com.flipperdevices.inappnotification.api.model

sealed class InAppNotification(
    val title: String,
    val descriptionId: Int,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    class SavedKey(
        title: String,
        descriptionId: Int,
        durationMs: Long
    ) : InAppNotification(title, descriptionId, durationMs)

    class UpdateReady(
        val action: () -> Unit,
        title: String,
        descriptionId: Int,
        durationMs: Long
    ) : InAppNotification(
        title,
        descriptionId,
        durationMs
    )
}
