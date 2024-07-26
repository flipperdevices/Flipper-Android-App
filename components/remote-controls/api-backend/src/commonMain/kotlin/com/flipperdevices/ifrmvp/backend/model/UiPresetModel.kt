package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UiPresetModel(
    @SerialName("id")
    val id: Long,
    @SerialName("infrared_file_id")
    val infraredFileId: Long,
    @SerialName("file_name")
    val fileName: String
)
