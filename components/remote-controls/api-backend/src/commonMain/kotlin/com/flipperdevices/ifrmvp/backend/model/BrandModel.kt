package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BrandModel")
class BrandModel(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("category_id")
    val categoryId: Long,
)
