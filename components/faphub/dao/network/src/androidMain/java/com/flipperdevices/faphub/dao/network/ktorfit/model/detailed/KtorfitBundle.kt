package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitBundle(
    @SerialName("_id") val id: String,
    @SerialName("filename") val filename: String,
    @SerialName("length") val length: Long?
)
