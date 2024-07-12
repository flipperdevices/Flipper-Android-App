package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignalResponse(
    @SerialName("signal_model")
    val signalModel: SignalModel,
    @SerialName("message")
    val message: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("data")
    val data: Data,
) {
    @Serializable
    data class Data(
        @SerialName("type")
        val type: String,
        @SerialName("icon_id")
        val iconId: String? = null,
        @SerialName("text")
        val text: String? = null
    )
}
