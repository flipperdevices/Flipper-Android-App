package com.flipperdevices.faphub.dao.network.ktorfit.model.detailed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitDetailedBuild(
    @SerialName("_id") val id: String,
    @SerialName("sdk") val sdk: KtorfitSdk,
    @SerialName("metadata") val metadata: KtorfitMetadata? = null
)
