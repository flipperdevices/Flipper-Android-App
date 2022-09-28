package com.flipperdevices.updater.model

sealed class UpdateCardState {
    object InProgress : UpdateCardState()

    data class NoUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class UpdateAvailable(
        val update: UpdateRequest,
        val isOtherChannel: Boolean
    ) : UpdateCardState()

    data class ChooseUpdateFromStorage(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class Error(val type: UpdateErrorType) : UpdateCardState()
}
