package com.flipperdevices.faphub.dao.network.retrofit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RetrofitSdk(
    @SerialName("_id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("target") val target: String,
    @SerialName("api") val api: String
)
