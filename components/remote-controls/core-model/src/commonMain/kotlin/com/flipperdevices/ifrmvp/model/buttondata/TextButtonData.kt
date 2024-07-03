package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TextButtonData(
    @SerialName("key_id")
    override val keyIdentifier: IfrKeyIdentifier,
    @SerialName("text")
    val text: String
) : SingleKeyButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.TEXT
}
