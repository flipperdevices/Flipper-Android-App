package com.flipperdevices.faphub.dao.network.ktorfit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitExceptionDetail(
    @SerialName("status")
    val status: String,
    @SerialName("details")
    val details: String
)
