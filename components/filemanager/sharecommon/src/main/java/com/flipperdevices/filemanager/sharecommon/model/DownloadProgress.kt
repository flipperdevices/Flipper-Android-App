package com.flipperdevices.filemanager.sharecommon.model

sealed class DownloadProgress(
    val progress: Long,
    val speedBytesInSecond: Long
) {
    class Fixed(
        progress: Long = 0,
        val totalSize: Long = Long.MAX_VALUE,
        speedBytesInSecond: Long = 0
    ) : DownloadProgress(progress, speedBytesInSecond) {
        fun toProgressFloat() = progress.toFloat() / totalSize.toFloat()
    }

    class Infinite(
        progress: Long = 0,
        speedBytesInSecond: Long = 0
    ) : DownloadProgress(progress, speedBytesInSecond)

    fun updateProgress(delta: Long): DownloadProgress {
        return when (this) {
            is Fixed -> Fixed(progress + delta, totalSize)
            is Infinite -> Infinite(progress + delta)
        }
    }

    fun updateSpeed(newSpeed: Long): DownloadProgress {
        return when (this) {
            is Fixed -> Fixed(progress, newSpeed, totalSize)
            is Infinite -> Infinite(progress, speedBytesInSecond)
        }
    }
}
