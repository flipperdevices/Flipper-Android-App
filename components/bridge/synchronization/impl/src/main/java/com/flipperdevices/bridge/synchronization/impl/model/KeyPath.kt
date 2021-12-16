package com.flipperdevices.bridge.synchronization.impl.model

import kotlinx.serialization.Serializable

@Serializable
data class KeyPath(
    val path: String,
    val name: String,
    val fileType: com.flipperdevices.bridge.dao.api.model.FlipperFileType,
    val byteSize: Int
)
