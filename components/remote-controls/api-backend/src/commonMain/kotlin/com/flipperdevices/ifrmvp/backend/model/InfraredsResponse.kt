package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InfraredsResponse(
    @SerialName("infrared_files")
    val infraredFiles: List<IfrFileModel>
)
