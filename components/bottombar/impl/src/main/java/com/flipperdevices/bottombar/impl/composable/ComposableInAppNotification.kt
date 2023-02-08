package com.flipperdevices.bottombar.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationState
import com.flipperdevices.bottombar.impl.viewmodel.InAppNotificationViewModel
import com.flipperdevices.core.ktx.android.observeAsState
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableInAppNotification(
    notificationRenderer: InAppNotificationRenderer,
    modifier: Modifier = Modifier,
    notificationViewModel: InAppNotificationViewModel = tangleViewModel(),
) {
    val notificationState by notificationViewModel.state().collectAsState()

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.observeAsState()
    LaunchedEffect(key1 = lifecycleState) {
        when (lifecycleState) {
            Lifecycle.Event.ON_RESUME -> notificationViewModel.onResume()
            else -> {
                notificationViewModel.onPause()
            }
        }
    }

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
