package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.updater.model.UpdateRequest

interface UpdaterCardApi {
    @Composable
    fun ComposableUpdaterCard(modifier: Modifier, onStartUpdateRequest: (UpdateRequest) -> Unit)
}
