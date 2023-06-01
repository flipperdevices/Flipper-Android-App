package com.flipperdevices.faphub.dao.network.retrofit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitDetailedVersion(
    @SerialName("_id") val id: String,
    @SerialName("application_id") val applicationUid: String,
    @SerialName("bundle_id") val bundleId: String,
    @SerialName("current_build_id") val buildId: String,
    @SerialName("version") val version: String,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("description") val description: String
)
