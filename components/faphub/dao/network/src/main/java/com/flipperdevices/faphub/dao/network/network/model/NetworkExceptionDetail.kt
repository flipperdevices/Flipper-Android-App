package com.flipperdevices.faphub.dao.network.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkExceptionDetail(
    @SerialName("status")
    val status: String,
    @SerialName("code")
    val code: Int,
    @SerialName("details")
    val details: String
)
