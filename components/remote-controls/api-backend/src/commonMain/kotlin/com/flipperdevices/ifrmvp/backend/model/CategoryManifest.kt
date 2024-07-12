package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryManifest(
    @SerialName("display_name")
    val displayName: String,
    @SerialName("singular_display_name")
    val singularDisplayName: String
)
