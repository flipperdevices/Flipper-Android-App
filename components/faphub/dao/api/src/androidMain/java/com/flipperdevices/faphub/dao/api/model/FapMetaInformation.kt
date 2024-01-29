package com.flipperdevices.faphub.dao.api.model

import com.flipperdevices.core.data.SemVer

data class FapMetaInformation(
    val version: SemVer,
    val sizeBytes: Long?
)
