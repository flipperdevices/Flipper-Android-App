package com.flipperdevices.updater.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class UpdateCardState {
    data object InProgress : UpdateCardState()

    data class NoUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class UpdateAvailable(
        val update: UpdateRequest,
        val isOtherChannel: Boolean
    ) : UpdateCardState()

    data class UpdateFromFile(
        val flipperVersion: FirmwareVersion,
        val updateVersion: FirmwareVersion
    ) : UpdateCardState()

    data class Error(val type: UpdateErrorType) : UpdateCardState()
}
