package com.flipperdevices.faphub.dao.network.retrofit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitVersionDetailed(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
    @SerialName("current_build") val currentBuild: RetrofitBuild,
    @SerialName("bundle") val bundle: RetrofitBundle,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("links") val links: RetrofitLinks,
    @SerialName("description") val description: String,
    @SerialName("changelog") val changelog: String
)
