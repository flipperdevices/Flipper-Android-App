package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.flipperdevices.updater.model.UpdateCardState

interface UpdaterUIApi {
    @Composable
    fun getUpdateCardState(): State<UpdateCardState>

    @Composable
    fun RenderUpdateButton(updateCardState: UpdateCardState)
}
