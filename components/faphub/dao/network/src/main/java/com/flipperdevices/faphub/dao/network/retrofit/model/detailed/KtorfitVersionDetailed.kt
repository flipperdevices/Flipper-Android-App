package com.flipperdevices.faphub.dao.network.retrofit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitVersionDetailed(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
    @SerialName("current_build") val currentBuild: KtorfitBuild,
    @SerialName("bundle") val bundle: KtorfitBundle,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("links") val links: KtorfitLinks,
    @SerialName("description") val description: String,
    @SerialName("changelog") val changelog: String
)
