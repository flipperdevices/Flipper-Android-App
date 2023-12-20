package com.flipperdevices.faphub.report.api

import kotlinx.serialization.Serializable

@Serializable
data class FapReportArgument(
    val applicationUid: String,
    val reportUrl: String
)
