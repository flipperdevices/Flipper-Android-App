package com.flipperdevices.updater.model

sealed class DownloadProgress {
    object NotStarted : DownloadProgress()

    data class InProgress(
        val processedBytes: Long,
        val totalBytes: Long?
    ) : DownloadProgress()

    data object Finished : DownloadProgress()
}
