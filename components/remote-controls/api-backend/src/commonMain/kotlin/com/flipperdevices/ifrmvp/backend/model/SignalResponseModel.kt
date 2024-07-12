package com.flipperdevices.ifrmvp.backend.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SignalResponseModel(
    @SerialName("signal_response")
    val signalResponse: SignalResponse? = null,
    @SerialName("ifr_file_model")
    val ifrFileModel: IfrFileModel? = null
)
