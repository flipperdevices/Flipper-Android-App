package com.flipperdevices.faphub.installation.button.api

import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.dao.api.model.FapItemVersion

data class FapButtonConfig(
    val applicationId: String,
    val version: FapItemVersion,
)

fun FapItemShort.toFapButtonConfig() = FapButtonConfig(
    applicationId = applicationId,
    version = upToDateVersion
)

fun FapItem.toFapButtonConfig() = FapButtonConfig(
    applicationId = applicationId,
    version = upToDateVersion
)
