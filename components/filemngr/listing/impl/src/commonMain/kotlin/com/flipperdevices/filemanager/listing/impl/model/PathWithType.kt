package com.flipperdevices.filemanager.listing.impl.model

import com.flipperdevices.bridge.connection.feature.storage.api.model.FileType
import com.flipperdevices.filemanager.util.serialization.PathSerializer
import kotlinx.serialization.Serializable
import okio.Path

@Serializable
data class PathWithType(
    val fileType: FileType,
    @Serializable(PathSerializer::class)
    val fullPath: Path,
    val size: Long
)
