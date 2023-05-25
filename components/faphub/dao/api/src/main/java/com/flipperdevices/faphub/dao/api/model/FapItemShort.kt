package com.flipperdevices.faphub.dao.api.model

import com.flipperdevices.core.data.SemVer
import kotlinx.collections.immutable.ImmutableList

data class FapItemShort(
    val id: String,
    val picUrl: String,
    val description: String,
    val name: String,
    val category: FapCategory,
    val screenshots: ImmutableList<String>,
    val applicationId: String,
    val currentVersion: SemVer
)
