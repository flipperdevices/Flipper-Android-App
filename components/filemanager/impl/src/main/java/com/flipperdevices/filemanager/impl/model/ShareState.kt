package com.flipperdevices.filemanager.impl.model

data class ShareState(
    val name: String,
    val downloadProgress: DownloadProgress = DownloadProgress.Infinite(),
    val processCompleted: Boolean = false
)
