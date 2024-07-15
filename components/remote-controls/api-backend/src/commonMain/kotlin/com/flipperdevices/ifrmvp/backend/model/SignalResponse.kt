package com.flipperdevices.ifrmvp.backend.model

import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import com.flipperdevices.ifrmvp.model.serialization.ButtonDataSerializer
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
    @Serializable(ButtonDataSerializer::class)
    val data: ButtonData,
)
