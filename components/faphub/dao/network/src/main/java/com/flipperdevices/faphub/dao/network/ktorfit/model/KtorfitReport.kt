package com.flipperdevices.faphub.dao.network.ktorfit.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorfitReport(
    @SerialName("description") val description: String,
    @SerialName("description_type") val descriptionType: String
)
