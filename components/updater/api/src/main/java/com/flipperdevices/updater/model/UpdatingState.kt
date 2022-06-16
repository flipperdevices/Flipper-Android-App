package com.flipperdevices.updater.model

class UpdatingStateWithRequest(
    val state: UpdatingState,
    val request: UpdateRequest?
)

sealed class UpdatingState(
    /**
     * true if this state represent stop of update process
     */
    val isFinalState: Boolean
) {
    object NotStarted : UpdatingState(true)

    object FailedDownload : UpdatingState(true)
    object FailedPrepare : UpdatingState(true)
    object FailedUpload : UpdatingState(true)

    data class DownloadingFromNetwork(
        val percent: Float
    ) : UpdatingState(false)

    data class UploadOnFlipper(
        val percent: Float
    ) : UpdatingState(false)

    object Rebooting : UpdatingState(true)

    object Complete : UpdatingState(true)

    /**
     * When update process is correct, but after restart version mismatch
     */
    object Failed : UpdatingState(true)
}
