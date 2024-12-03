package com.flipperdevices.filemanager.download.model

import okio.Path

data class DownloadableFile(
    val fullPath: Path,
    val size: Long
)
