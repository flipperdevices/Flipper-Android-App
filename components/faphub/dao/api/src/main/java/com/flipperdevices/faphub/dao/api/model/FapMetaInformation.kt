package com.flipperdevices.faphub.dao.api.model

import androidx.compose.runtime.Stable
import com.flipperdevices.core.data.SemVer

@Stable
data class FapMetaInformation(
    val version: SemVer,
    val sizeBytes: Long?
)
