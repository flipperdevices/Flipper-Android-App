package com.flipperdevices.faphub.dao.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationVersionShort(
    @SerialName("_id") val id: String,
    @SerialName("version") val version: String,
)
