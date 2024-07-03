package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CategoriesRequestBody")
class CategoriesRequestBody(
    @SerialName("language_code")
    val languageCode: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null
)
