package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("VOL")
@Serializable
data class VolumeButtonData(
    @SerialName("add_key_id")
    val addKeyIdentifier: IfrKeyIdentifier,
    @SerialName("reduce_key_id")
    val reduceKeyIdentifier: IfrKeyIdentifier,
) : ButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.VOLUME
}
