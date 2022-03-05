package com.flipperdevices.inappnotification.api

import androidx.compose.runtime.Composable
import com.flipperdevices.inappnotification.api.model.InAppNotification

interface InAppNotificationRenderer {
    @Composable
    fun InAppNotification(notification: InAppNotification)
}
