package com.flipperdevices.updater.model

data class UpdatingStateWithRequest(
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

    object FailedInternalStorage : UpdatingState(true)
    object FailedSubGhzProvisioning : UpdatingState(true)
    object FailedOutdatedApp : UpdatingState(true)
    object FailedDownload : UpdatingState(true)
    object FailedPrepare : UpdatingState(true)
    object FailedUpload : UpdatingState(true)
    object FailedCustomUpdate : UpdatingState(true)

    object SubGhzProvisioning : UpdatingState(false)

    data class DownloadingFromNetwork(
        val percent: Float
    ) : UpdatingState(false)

    data class UploadOnFlipper(
        val percent: Float
    ) : UpdatingState(false)

    data object Rebooting : UpdatingState(true)

    data object Complete : UpdatingState(true)

    /**
     * When update process is correct, but after restart version mismatch
     */
    data object Failed : UpdatingState(true)
}
