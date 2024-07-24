package com.flipperdevices.inappnotification.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.inappnotification.api.model.InAppNotification

interface InAppNotificationRenderer {
    @Composable
    fun InAppNotification(
        notification: InAppNotification,
        modifier: Modifier,
        onNotificationHide: () -> Unit
    )
}
