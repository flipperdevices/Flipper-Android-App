package com.flipperdevices.faphub.dao.api.model

import com.flipperdevices.core.data.SemVer

data class FapItemVersion(
    val id: String,
    val version: SemVer
)
