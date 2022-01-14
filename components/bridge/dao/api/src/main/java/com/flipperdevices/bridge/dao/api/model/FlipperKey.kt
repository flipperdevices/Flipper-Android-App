package com.flipperdevices.bridge.dao.api.model

data class FlipperKey(
    val name: String, // With extension
    val fileType: FlipperFileType,
    val keyContent: FlipperKeyContent
)
