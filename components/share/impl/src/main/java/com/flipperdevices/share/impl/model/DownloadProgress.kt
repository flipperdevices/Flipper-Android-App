package com.flipperdevices.share.impl.model

data class DownloadProgress(
    val progress: Long = 0,
    val totalSize: Long = Long.MAX_VALUE,
) {
    fun toProgressFloat() = progress.toFloat() / totalSize.toFloat()
}
