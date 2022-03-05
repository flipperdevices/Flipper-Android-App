package com.flipperdevices.inappnotification.api.model

data class InAppNotification(
    val title: String,
    val descriptionId: Int,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)
