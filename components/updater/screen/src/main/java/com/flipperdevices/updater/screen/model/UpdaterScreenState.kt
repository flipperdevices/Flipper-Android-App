package com.flipperdevices.updater.screen.model

import com.flipperdevices.updater.model.VersionFiles

sealed class UpdaterScreenState(
    val firmwareData: VersionFiles?
) {
    object NotStarted : UpdaterScreenState(null)

    class CancelingSynchronization(firmwareData: VersionFiles?) : UpdaterScreenState(firmwareData)

    class DownloadingFromNetwork(
        firmwareData: VersionFiles?,
        val percent: Float
    ) : UpdaterScreenState(firmwareData)

    class UploadOnFlipper(
        firmwareData: VersionFiles?,
        val percent: Float
    ) : UpdaterScreenState(firmwareData)

    class Failed(
        val failedReason: FailedReason
    ) : UpdaterScreenState(null)

    object Rebooting : UpdaterScreenState(null)

    object CancelingUpdate : UpdaterScreenState(null)

    object Finish : UpdaterScreenState(null)
}
