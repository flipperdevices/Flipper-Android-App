package com.flipperdevices.filemanager.impl.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ShareFile(
    val name: String,
    val flipperFilePath: String,
    val size: Long
) : Parcelable {
    constructor(fileItem: FileItem) : this(fileItem.fileName, fileItem.path, fileItem.size)
}
