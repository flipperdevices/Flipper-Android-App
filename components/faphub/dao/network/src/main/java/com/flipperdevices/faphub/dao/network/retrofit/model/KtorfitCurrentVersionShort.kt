package com.flipperdevices.faphub.dao.network.retrofit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitCurrentVersionShort(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
    @SerialName("icon") val iconUrl: String,
    @SerialName("screenshots") val screenshots: List<String>,
    @SerialName("description") val description: String
)
