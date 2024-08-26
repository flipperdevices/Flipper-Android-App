package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OkNavigationButtonData(
    @SerialName("up_key_id")
    val upKeyIdentifier: IfrKeyIdentifier,
    @SerialName("left_key_id")
    val leftKeyIdentifier: IfrKeyIdentifier,
    @SerialName("down_key_id")
    val downKeyIdentifier: IfrKeyIdentifier,
    @SerialName("right_key_id")
    val rightKeyIdentifier: IfrKeyIdentifier,
    @SerialName("ok_key_id")
    val okKeyIdentifier: IfrKeyIdentifier,
) : ButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.OK_NAVIGATION
}
