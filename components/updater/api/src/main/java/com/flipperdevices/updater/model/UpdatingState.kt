package com.flipperdevices.updater.model

class UpdatingStateWithRequest(
    val state: UpdatingState,
    val request: UpdateRequest?
)

sealed class UpdatingState {
    object NotStarted : UpdatingState()

    object FailedDownload : UpdatingState()
    object FailedPrepare : UpdatingState()
    object FailedUpload : UpdatingState()

    data class DownloadingFromNetwork(
        val percent: Float
    ) : UpdatingState()

    data class UploadOnFlipper(
        val percent: Float
    ) : UpdatingState()

    object Rebooting : UpdatingState()
}
