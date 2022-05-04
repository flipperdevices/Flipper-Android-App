package com.flipperdevices.updater.ui.model

import com.flipperdevices.updater.model.FirmwareVersion

sealed class UpdaterScreenState(
    val version: FirmwareVersion?
) {
    object NotStarted : UpdaterScreenState(null)

    class CancelingSynchronization(version: FirmwareVersion?) : UpdaterScreenState(version)

    class DownloadingFromNetwork(
        version: FirmwareVersion?,
        val percent: Float
    ) : UpdaterScreenState(version)

    class UploadOnFlipper(
        version: FirmwareVersion?,
        val percent: Float
    ) : UpdaterScreenState(version)

    object CancelingUpdate : UpdaterScreenState(null)

    object Finish : UpdaterScreenState(null)
}
