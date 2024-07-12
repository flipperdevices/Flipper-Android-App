package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IconButtonData(
    @SerialName("key_id")
    override val keyIdentifier: IfrKeyIdentifier,
    @SerialName("icon_id")
    val iconId: IconType
) : SingleKeyButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.ICON

    enum class IconType {
        BACK, HOME, INFO, MORE, MUTE, POWER,
        COOL, HEAT, FAN
    }
}
