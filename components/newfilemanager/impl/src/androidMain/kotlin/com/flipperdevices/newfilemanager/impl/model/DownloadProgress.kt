package com.flipperdevices.newfilemanager.impl.model

sealed class DownloadProgress {
    data class Fixed(
        val progressInternal: Long = 0,
        val totalSize: Long
    ) : DownloadProgress() {
        fun toProgressFloat() = if (totalSize != 0L) {
            progress.toFloat() / totalSize.toFloat()
        } else {
            0f
        }
    }

    data class Infinite(
        val progressInternal: Long = 0
    ) : DownloadProgress()

    val progress: Long
        get() = when (this) {
            is Fixed -> progressInternal
            is Infinite -> progressInternal
        }
}
