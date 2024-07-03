package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Base64ImageButtonData(
    @SerialName("key_id")
    override val keyIdentifier: IfrKeyIdentifier,
    @SerialName("png_base64")
    val pngBase64: String,
) : SingleKeyButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.BASE64_IMAGE
}
