package com.flipperdevices.notification.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext

@Immutable
interface FlipperAppNotificationDialogApi {
    @Composable
    @Suppress("NonSkippableComposable")
    fun NotificationDialog(componentContext: ComponentContext)
}
