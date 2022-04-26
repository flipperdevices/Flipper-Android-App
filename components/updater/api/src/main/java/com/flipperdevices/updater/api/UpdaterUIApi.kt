package com.flipperdevices.updater.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.flipperdevices.updater.model.UpdateCardState

interface UpdaterUIApi {
    @Composable
    fun getUpdateCardApi(): UpdateCardApi

    @Composable
    fun isUpdaterAvailable(): State<Boolean>

    @Composable
    fun RenderUpdateButton(updateCardState: UpdateCardState)
}
