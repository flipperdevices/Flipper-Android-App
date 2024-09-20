package com.flipperdevices.bridge.connection.feature.storage.impl.fm.listing

import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.protobuf.storage.File

fun File.FileType.toInternalType(): FileType? = when (this) {
    File.FileType.DIR -> FileType.DIR
    File.FileType.FILE -> FileType.FILE
    is File.FileType.Unrecognized -> null
}
