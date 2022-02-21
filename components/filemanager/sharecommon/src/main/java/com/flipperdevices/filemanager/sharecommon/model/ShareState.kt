package com.flipperdevices.filemanager.sharecommon.model

data class ShareState(
    val downloadProgress: DownloadProgress = DownloadProgress.Infinite(),
    val processCompleted: Boolean = false,
    val dialogShown: Boolean = true
)
