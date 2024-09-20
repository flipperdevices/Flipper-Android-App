package com.flipperdevices.bridge.connection.feature.storage.api.model

data class ListingItem(
    val fileName: String,
    val fileType: FileType?,
    val size: Long
)
