package com.flipperdevices.ifrmvp.model.serialization

import com.flipperdevices.ifrmvp.model.buttondata.Base64ImageButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ChannelButtonData
import com.flipperdevices.ifrmvp.model.buttondata.IconButtonData
import com.flipperdevices.ifrmvp.model.buttondata.NavigationButtonData
import com.flipperdevices.ifrmvp.model.buttondata.OkNavigationButtonData
import com.flipperdevices.ifrmvp.model.buttondata.PowerButtonData
import com.flipperdevices.ifrmvp.model.buttondata.ShutterButtonData
import com.flipperdevices.ifrmvp.model.buttondata.TextButtonData
import com.flipperdevices.ifrmvp.model.buttondata.UnknownButtonData
import com.flipperdevices.ifrmvp.model.buttondata.VolumeButtonData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

internal class ButtonDataEncoder(private val json: Json) {
    fun encodeToJsonObject(buttonData: ButtonData): JsonObject {
        return when (buttonData.type) {
            ButtonData.ButtonType.UNKNOWN -> {
                buttonData as UnknownButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.BASE64_IMAGE -> {
                buttonData as Base64ImageButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.TEXT -> {
                buttonData as TextButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.CHANNEL -> {
                buttonData as ChannelButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.OK_NAVIGATION -> {
                buttonData as OkNavigationButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.VOLUME -> {
                buttonData as VolumeButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.ICON -> {
                buttonData as IconButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.SHUTTER -> {
                buttonData as ShutterButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.NAVIGATION -> {
                buttonData as NavigationButtonData
                json.encodeToJsonElement(buttonData)
            }

            ButtonData.ButtonType.POWER -> {
                buttonData as PowerButtonData
                json.encodeToJsonElement(buttonData)
            }
        }.jsonObject
    }
}
