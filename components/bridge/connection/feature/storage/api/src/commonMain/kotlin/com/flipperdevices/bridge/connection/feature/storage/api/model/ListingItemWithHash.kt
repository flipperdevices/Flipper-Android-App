package com.flipperdevices.bridge.connection.feature.storage.api.model

data class ListingItemWithHash(
    val fileName: String,
    val fileType: FileType?,
    val size: Long,
    val md5: String
)
