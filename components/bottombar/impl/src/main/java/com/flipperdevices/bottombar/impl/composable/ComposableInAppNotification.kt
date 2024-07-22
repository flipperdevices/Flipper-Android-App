package com.flipperdevices.bottombar.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationState
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.flipperdevices.inappnotification.api.model.InAppNotification

@Composable
fun ComposableInAppNotification(
    notificationRenderer: InAppNotificationRenderer,
    notificationState: InAppNotificationState,
    onNotificationHide: (notification: InAppNotification) -> Unit,
    modifier: Modifier = Modifier
) {
    if (notificationState !is InAppNotificationState.ShownNotification) {
        return
    }
    notificationRenderer.InAppNotification(
        notification = notificationState.notification,
        modifier = modifier
    ) {
        onNotificationHide(notificationState.notification)
    }
}
