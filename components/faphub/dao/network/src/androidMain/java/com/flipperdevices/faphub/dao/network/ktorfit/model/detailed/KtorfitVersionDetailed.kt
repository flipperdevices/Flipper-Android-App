package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import com.flipperdevices.faphub.dao.network.ktorfit.model.KtorfitBuildState
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitVersionDetailed(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
    @SerialName("current_build") val currentBuild: KtorfitDetailedBuild,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("links") val links: KtorfitLinks,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("description") val description: String,
    @SerialName("changelog") val changelog: String,
    @SerialName("name") val name: String,
    @SerialName("status") val status: KtorfitBuildState
)
