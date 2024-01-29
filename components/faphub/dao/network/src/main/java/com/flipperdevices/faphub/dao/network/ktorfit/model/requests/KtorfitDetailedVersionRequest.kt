package com.flipperdevices.faphub.dao.network.ktorfit.model.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitDetailedVersionRequest(
    @SerialName("application_versions")
    val applicationVersions: List<String>,
    @SerialName("limit")
    val limit: Int,
    @SerialName("offset")
    val offset: Int = 0
)
