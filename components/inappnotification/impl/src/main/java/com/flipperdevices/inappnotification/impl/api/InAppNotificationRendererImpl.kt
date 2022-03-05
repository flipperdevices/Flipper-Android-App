package com.flipperdevices.inappnotification.impl.api

import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.inappnotification.api.InAppNotificationRenderer
import com.flipperdevices.inappnotification.api.model.InAppNotification
import com.flipperdevices.inappnotification.impl.composable.ComposableInAppNotification
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class InAppNotificationRendererImpl @Inject constructor() : InAppNotificationRenderer {
    @Composable
    override fun InAppNotification(
        notification: InAppNotification,
        onNotificationHidden: () -> Unit
    ) {
        ComposableInAppNotification(notification, onNotificationHidden)
    }
}
