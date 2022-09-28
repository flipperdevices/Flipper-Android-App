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

    data class CustomUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class Error(val type: UpdateErrorType) : UpdateCardState()

    fun getFWVersion(): FirmwareVersion? {
        return when (this) {
            is CustomUpdate -> this.flipperVersion
            is Error -> null
            InProgress -> null
            is NoUpdate -> this.flipperVersion
            is UpdateAvailable -> this.update.updateTo
        }
    }
}
