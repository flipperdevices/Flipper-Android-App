package com.flipperdevices.bottombar.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationState
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationViewModel
import com.flipperdevices.core.ktx.android.OnLifecycleEvent
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableInAppNotification(
    notificationRenderer: InAppNotificationRenderer,
    modifier: Modifier = Modifier,
    notificationViewModel: InAppNotificationViewModel = tangleViewModel(),
) {
    val notificationState by notificationViewModel.state().collectAsState()

    OnLifecycleEvent(onEvent = notificationViewModel::onLifecycleEvent)

    val localNotificationState = notificationState
    if (localNotificationState !is InAppNotificationState.ShownNotification) {
        return
    }
    notificationRenderer.InAppNotification(
        notification = localNotificationState.notification,
        modifier = modifier
    ) {
        notificationViewModel.onNotificationHidden(
            notification = localNotificationState.notification
        )
    }
}
