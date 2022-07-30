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
            is Fixed -> Fixed(
                progress = progress + delta,
                totalSize = totalSize
            )
            is Infinite -> Infinite(progress = progress + delta)
        }
    }

    fun updateSpeed(newSpeed: Long): DownloadProgress {
        return when (this) {
            is Fixed -> Fixed(
                progress = progress,
                speedBytesInSecond = newSpeed,
                totalSize = totalSize
            )
            is Infinite -> Infinite(
                progress = progress,
                speedBytesInSecond = speedBytesInSecond
            )
        }
    }
}
