package com.flipperdevices.ifrmvp.model.buttondata

import kotlinx.serialization.Serializable

/**
 * [ButtonData] is specific model for specific button
 */
@Serializable
sealed interface ButtonData {
    val type: ButtonType

    enum class ButtonType {
        UNKNOWN,
        TEXT,
        ICON,
        BASE64_IMAGE,
        CHANNEL,
        OK_NAVIGATION,
        NAVIGATION,
        VOLUME,
        SHUTTER,
        POWER
    }
}
