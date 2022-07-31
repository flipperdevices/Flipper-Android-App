package com.flipperdevices.filemanager.sharecommon.model

data class ShareState(
    val name: String,
    val downloadProgress: DownloadProgress = DownloadProgress.Infinite(),
    val processCompleted: Boolean = false,
    val dialogShown: Boolean = true
)
