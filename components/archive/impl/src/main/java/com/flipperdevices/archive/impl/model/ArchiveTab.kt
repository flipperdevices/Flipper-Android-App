package com.flipperdevices.archive.impl.model

import com.flipperdevices.bridge.dao.api.model.FlipperFileType

sealed class ArchiveTab {
    object General : ArchiveTab()

    data class Specified(
        val fileType: FlipperFileType
    ) : ArchiveTab()
}
