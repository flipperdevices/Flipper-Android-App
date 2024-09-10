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
        ADD_PLUS_MORE,
        AUX,
        BACK,
        BRIGHT_LESS,
        BRIGHT_MORE,
        CH_DOWN,
        CH_UP,
        COLD_WIND,
        COOL,
        DELETE,
        DOWN,
        EJECT,
        ENERGY_SAVE,
        EXIT,
        FAN_HIGH,
        FAN_MEDIUM,
        FAN_LOW,
        FAN_OFF,
        FAN_SPEED,
        FAN_SPEED_DOWN,
        FAN_SPEED_UP,
        FAR,
        FAVORITE,
        FOCUS_LESS,
        FOCUS_MORE,
        FORWARD,
        HEAT_ADD,
        HEAT_REDUCE,
        HOME,
        INFO,
        LEFT,
        LIGHT,
        LIVE_TV,
        MENU,
        MODE,
        MUTE,
        NEAR,
        NEXT,
        OK,
        OSCILLATE,
        PAUSE,
        PREVIOUS,
        POWER,
        RECORD,
        REMOVE_MINUS_LESS,
        RESET,
        REWIND,
        RIGHT,
        SETTINGS,
        SHAKE_WIND,
        CAMERA,
        SLEEP,
        STOP,
        SWING,
        TEMPERATURE_DOWN,
        TEMPERATURE_UP,
        TIMER,
        TIMER_ADD,
        TIMER_REDUCE,
        TV,
        UP,
        VOD,
        VOL_DOWN,
        VOL_UP,
        WIND_SPEED,
        WIND_TYPE,
        ZOOM_OUT,
        ZOOM_IN,
        PLAY,
        MORE,
        HEAT,
    }
}
