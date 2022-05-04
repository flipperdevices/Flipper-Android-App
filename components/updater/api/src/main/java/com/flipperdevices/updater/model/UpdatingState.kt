package com.flipperdevices.updater.model

class UpdatingStateWithVersion(
    val state: UpdatingState,
    val version: FirmwareVersion?
)

sealed class UpdatingState {
    object NotStarted : UpdatingState()

    data class DownloadingFromNetwork(
        val percent: Float
    ) : UpdatingState()

    data class UploadOnFlipper(
        val percent: Float
    ) : UpdatingState()

    object Rebooting : UpdatingState()
}
