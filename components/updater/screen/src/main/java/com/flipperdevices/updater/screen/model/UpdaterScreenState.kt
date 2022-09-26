package com.flipperdevices.updater.screen.model

import com.flipperdevices.updater.model.UpdateRequest

sealed class UpdaterScreenState(
    val updateRequest: UpdateRequest?
) {
    object NotStarted : UpdaterScreenState(null)

    class CancelingSynchronization(
        updateRequest: UpdateRequest?
    ) : UpdaterScreenState(updateRequest)

    class SubGhzProvisioning(
        updateRequest: UpdateRequest?
    ) : UpdaterScreenState(updateRequest)

    class DownloadingFromNetwork(
        updateRequest: UpdateRequest?,
        val percent: Float
    ) : UpdaterScreenState(updateRequest)

    class UploadOnFlipper(
        updateRequest: UpdateRequest?,
        val percent: Float
    ) : UpdaterScreenState(updateRequest)

    class Failed(
        val failedReason: FailedReason
    ) : UpdaterScreenState(null)

    object Rebooting : UpdaterScreenState(null)

    object CancelingUpdate : UpdaterScreenState(null)

    object Finish : UpdaterScreenState(null)
}
