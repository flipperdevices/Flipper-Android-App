package com.flipperdevices.updater.model

sealed class UpdateCardState {
    object InProgress : UpdateCardState()

    data class NoUpdate(
        val flipperVersion: FirmwareVersion
    ) : UpdateCardState()

    data class UpdateAvailable constructor(
        val fromVersion: FirmwareVersion,
        val lastVersion: VersionFiles,
        val isOtherChannel: Boolean
    ) : UpdateCardState()

    data class Error(val type: UpdateErrorType) : UpdateCardState()
}
