package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IfrFileModel(
    @SerialName("id")
    val id: Long,
    @SerialName("category_id")
    val categoryId: Long,
    @SerialName("brand_id")
    val brandId: Long
)
