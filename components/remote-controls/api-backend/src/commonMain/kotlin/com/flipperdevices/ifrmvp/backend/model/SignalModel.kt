package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignalModel(
    @SerialName("id")
    val id: Long,
    @SerialName("ifr_file_id")
    val irFileId: Long,
    @SerialName("brand_id")
    val brandId: Long,
    @SerialName("category_id")
    val categoryId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("type")
    val type: String,
    @SerialName("protocol")
    val protocol: String? = null,
    @SerialName("address")
    val address: String? = null,
    @SerialName("command")
    val command: String? = null,
    @SerialName("frequency")
    val frequency: String? = null,
    @SerialName("duty_cycle")
    val dutyCycle: String? = null,
    @SerialName("data")
    val data: String? = null,
    @SerialName("hash")
    val hash: String
)
