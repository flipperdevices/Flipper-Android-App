package com.flipperdevices.ifrmvp.model.buttondata

import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IconButtonData(
    @SerialName("key_id")
    override val keyIdentifier: IfrKeyIdentifier = IfrKeyIdentifier.Unknown,
    @SerialName("icon_id")
    val iconId: IconType
) : SingleKeyButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.ICON

    enum class IconType {
        BACK, HOME, INFO, MORE, MUTE,
        POWER, COOL, HEAT, FAN, CAMERA,
        BRIGHT_MORE, BRIGHT_LESS,
        PAUSE, PLAY, STOP, EXIT,
        MENU, ZOOM_IN, ZOOM_OUT,
        RESET, NEXT, PREVIOUS,
        EJECT, RECORD, WIND_SPEED,
        MODE, LIGHT, TIMER, OFF,
        DELETE, LIVE_TV, FAVORITE,
        ENERGY_SAVE, VOL_UP, VOL_DOWN
    }
}
