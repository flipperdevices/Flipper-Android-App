package com.flipperdevices.share.impl.model

data class ShareState(
    val downloadProgress: DownloadProgress = DownloadProgress(),
    val dialogShown: Boolean = true
)
