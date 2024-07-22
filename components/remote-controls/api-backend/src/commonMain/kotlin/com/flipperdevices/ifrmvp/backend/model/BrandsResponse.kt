package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BrandsResponse")
data class BrandsResponse(
    @SerialName("brands")
    val brands: List<BrandModel>
)
