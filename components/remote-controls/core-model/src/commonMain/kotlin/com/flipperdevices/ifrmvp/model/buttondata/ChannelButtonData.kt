package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("CHANNEL")
@Serializable
data class ChannelButtonData(
    @SerialName("add_key_id")
    val addKeyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
    @SerialName("reduce_key_id")
    val reduceKeyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
) : ButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.CHANNEL
}
