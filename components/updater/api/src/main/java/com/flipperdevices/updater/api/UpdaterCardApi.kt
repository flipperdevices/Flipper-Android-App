package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.updater.model.UpdateRequest

@Immutable
interface UpdaterCardApi {
    @Suppress("ComposableNaming")
    @Composable
    fun ComposableUpdaterCard(
        modifier: Modifier,
        deeplink: Deeplink?,
        onStartUpdateRequest: (UpdateRequest) -> Unit,
        requestRefresh: Boolean,
        onRefreshRequestExecuted: () -> Unit
    )
}
