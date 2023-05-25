package com.flipperdevices.faphub.installation.button.api

import com.flipperdevices.core.data.SemVer
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort

data class FapButtonConfig(
    val applicationId: String,
    val version: SemVer,
)

fun FapItemShort.toFapButtonConfig() = FapButtonConfig(
    applicationId = applicationId,
    version = currentVersion
)

fun FapItem.toFapButtonConfig() = FapButtonConfig(
    applicationId = applicationId,
    version = currentVersion
)