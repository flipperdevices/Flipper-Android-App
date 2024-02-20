package com.flipperdevices.notification.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.notification.api.FlipperAppNotificationDialogApi
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FlipperAppNotificationDialogApi::class)
class FlipperAppNotificationDialogApiNoopImpl @Inject constructor() :
    FlipperAppNotificationDialogApi {
    @Composable
    override fun NotificationDialog(componentContext: ComponentContext) = Unit
}
