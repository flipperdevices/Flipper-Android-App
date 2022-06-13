package com.flipperdevices.updater.model

sealed class UpdateCardState {
    object InProgress : UpdateCardState()

    data class NoUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class UpdateAvailable(
        val lastVersion: FirmwareVersion,
        val updaterDist: DistributionFile,
        val isOtherChannel: Boolean
    ) : UpdateCardState()

    data class Error(val type: UpdateErrorType) : UpdateCardState()
}
