package com.flipperdevices.share.common.model

data class ShareState(
    val downloadProgress: DownloadProgress = DownloadProgress.Infinite(),
    val dialogShown: Boolean = true
)
