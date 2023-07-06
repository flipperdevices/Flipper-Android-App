package com.flipperdevices.faphub.dao.network.ktorfit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitCurrentVersionShort(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
    @SerialName("icon_uri") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("short_description") val shortDescription: String,
    @SerialName("name") val name: String,
    @SerialName("status") val status: KtorfitBuildState
)
