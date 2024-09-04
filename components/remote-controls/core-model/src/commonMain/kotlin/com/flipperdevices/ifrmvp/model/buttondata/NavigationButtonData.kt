package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NavigationButtonData(
    @SerialName("up_key_id")
    val upKeyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
    @SerialName("left_key_id")
    val leftKeyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
    @SerialName("down_key_id")
    val downKeyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
    @SerialName("right_key_id")
    val rightKeyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
) : ButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.NAVIGATION
}
