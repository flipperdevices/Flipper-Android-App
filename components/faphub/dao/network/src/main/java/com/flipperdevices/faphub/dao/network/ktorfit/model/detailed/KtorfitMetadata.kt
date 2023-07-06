package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitMetadata(
    @SerialName("length") val sizeBytes: Long? = null
)
