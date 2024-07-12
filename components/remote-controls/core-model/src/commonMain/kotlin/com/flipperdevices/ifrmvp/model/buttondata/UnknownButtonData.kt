package com.flipperdevices.ifrmvp.model.buttondata

import kotlinx.serialization.Serializable

@Serializable
data object UnknownButtonData : ButtonData {
    override val type: ButtonData.ButtonType = ButtonData.ButtonType.UNKNOWN
}
