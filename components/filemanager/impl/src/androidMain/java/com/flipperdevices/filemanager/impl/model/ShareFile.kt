package com.flipperdevices.filemanager.impl.model

import kotlinx.serialization.Serializable

@Serializable
data class ShareFile(
    val name: String,
    val flipperFilePath: String,
    val size: Long
) {
    constructor(fileItem: FileItem) : this(fileItem.fileName, fileItem.path, fileItem.size)
}
