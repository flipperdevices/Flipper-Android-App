package com.flipperdevices.faphub.dao.network.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkException(
    @SerialName("detail")
    val detail: NetworkExceptionDetail
)
