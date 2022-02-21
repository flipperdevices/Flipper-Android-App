package com.flipperdevices.filemanager.api.share

data class ShareFile(
    val name: String,
    val flipperFilePath: String,
    val size: Long
)
