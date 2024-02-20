package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.model.UpdateRequest

@Immutable
interface UpdaterCardApi {
    @Suppress("NonSkippableComposable")
    @Composable
    fun ComposableUpdaterCard(
        modifier: Modifier,
        componentContext: ComponentContext,
        deeplink: Deeplink.BottomBar.DeviceTab.WebUpdate?,
        onStartUpdateRequest: (UpdateRequest) -> Unit,
        requestRefresh: Boolean,
        onRefreshRequestExecuted: () -> Unit
    )
}
