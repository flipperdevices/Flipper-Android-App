package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.updater.model.UpdateRequest

interface UpdaterCardApi {
    @Suppress("ComposableNaming")
    @Composable
    fun ComposableUpdaterCard(
        modifier: Modifier,
        onStartUpdateRequest: (UpdateRequest) -> Unit,
        requestRefresh: Boolean,
        onRefreshRequestExecuted: () -> Unit
    )
}
