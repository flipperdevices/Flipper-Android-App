package com.flipperdevices.faphub.dao.network.ktorfit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitException(
    @SerialName("detail")
    val detail: KtorfitExceptionDetail
)
