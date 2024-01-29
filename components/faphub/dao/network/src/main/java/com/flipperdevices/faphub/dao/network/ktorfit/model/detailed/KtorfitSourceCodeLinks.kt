package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitSourceCodeLinks(
    @SerialName("type") val type: String,
    @SerialName("uri") val url: String
)
