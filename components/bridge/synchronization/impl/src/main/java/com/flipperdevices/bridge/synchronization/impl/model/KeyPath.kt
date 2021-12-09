package com.flipperdevices.bridge.synchronization.impl.model

import com.flipperdevices.bridge.dao.FlipperFileType
import kotlinx.serialization.Serializable

@Serializable
data class KeyPath(
    val path: String,
    val name: String,
    val fileType: FlipperFileType,
    val byteSize: Int
)
