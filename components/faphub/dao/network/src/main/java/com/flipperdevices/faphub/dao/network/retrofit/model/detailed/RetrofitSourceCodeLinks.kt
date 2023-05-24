package com.flipperdevices.faphub.dao.network.retrofit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitSourceCodeLinks(
    @SerialName("type") val type: String,
    @SerialName("uri") val url: String
)
