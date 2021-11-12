package com.flipperdevices.share.common.model

sealed class DownloadProgress(
    val progress: Long
) {
    class Fixed(
        progress: Long = 0,
        val totalSize: Long = Long.MAX_VALUE,
    ) : DownloadProgress(progress) {
        fun updateTotalSize(size: Long): Fixed {
            return Fixed(progress, size)
        }

        fun toProgressFloat() = progress.toFloat() / totalSize.toFloat()
    }

    class Infinite(progress: Long = 0) : DownloadProgress(progress)

    fun updateProgress(delta: Long): DownloadProgress {
        return when (this) {
            is Fixed -> Fixed(progress + delta, totalSize)
            is Infinite -> Infinite(progress + delta)
        }
    }
}
