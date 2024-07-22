package com.flipperdevices.ifrmvp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PageLayout(
    @SerialName("buttons")
    val buttons: List<IfrButton>
)
