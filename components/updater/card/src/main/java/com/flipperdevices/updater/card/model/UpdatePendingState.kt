package com.flipperdevices.updater.card.model

import com.flipperdevices.updater.model.UpdateRequest

sealed class UpdatePendingState {
    object LowBattery : UpdatePendingState()
    object FileExtension : UpdatePendingState()
    object FileBig : UpdatePendingState()
    data class Ready(val request: UpdateRequest, val isSyncing: Boolean) : UpdatePendingState()
}
