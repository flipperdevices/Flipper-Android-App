package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PowerButtonData(
    @SerialName("key_id")
    override val keyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
) : SingleKeyButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.POWER
}
