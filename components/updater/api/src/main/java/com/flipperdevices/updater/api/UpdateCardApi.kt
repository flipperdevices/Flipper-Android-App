package com.flipperdevices.updater.api

import com.flipperdevices.updater.model.FirmwareChannel
import com.flipperdevices.updater.model.UpdateCardState
import kotlinx.coroutines.flow.StateFlow

interface UpdateCardApi {
    fun getUpdateCardState(): StateFlow<UpdateCardState>
    fun onSelectChannel(channel: FirmwareChannel?)
    fun retry()
}
