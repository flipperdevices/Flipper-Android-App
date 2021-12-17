package com.flipperdevices.archive.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperFileType

// If null, show all keys
data class ArchiveTab(
    val fileType: FlipperFileType? = null
)
