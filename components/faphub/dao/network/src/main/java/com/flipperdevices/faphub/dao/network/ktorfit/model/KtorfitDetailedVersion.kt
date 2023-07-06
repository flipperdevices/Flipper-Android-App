package com.flipperdevices.faphub.dao.network.ktorfit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitDetailedVersion(
    @SerialName("_id") val id: String,
    @SerialName("application_id") val applicationUid: String,
    @SerialName("bundle_id") val bundleId: String,
    @SerialName("version") val version: String,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("short_description") val shortDescription: String,
)
