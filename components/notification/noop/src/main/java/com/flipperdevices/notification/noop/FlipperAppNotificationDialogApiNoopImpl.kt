package com.flipperdevices.notification.noop

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.notification.api.FlipperAppNotificationDialogApi
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<FlipperAppNotificationDialogApi>())
class FlipperAppNotificationDialogApiNoopImpl @Inject constructor() :
    FlipperAppNotificationDialogApi {
    @Composable
    override fun NotificationDialog(componentContext: ComponentContext) = Unit
}
