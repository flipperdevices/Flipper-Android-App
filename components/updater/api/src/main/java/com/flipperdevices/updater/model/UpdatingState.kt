package com.flipperdevices.updater.model

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
