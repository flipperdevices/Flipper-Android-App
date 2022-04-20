package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import com.flipperdevices.updater.model.UpdateCardState

interface UpdaterUIApi {
    @Composable
    fun getUpdateCardApi(): UpdateCardApi

    @Composable
    fun RenderUpdateButton(updateCardState: UpdateCardState)
}
