package com.flipperdevices.ifrmvp.model.serialization

import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object ButtonDataSerializer : KSerializer<ButtonData> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor(
        serialName = "Flipper.ButtonData",
    )

    private val json: Json = Json {
        encodeDefaults = true
    }

    override fun deserialize(decoder: Decoder): ButtonData {
        val jsonObject = JsonObject.serializer().deserialize(decoder)
        return ButtonDataDecoder(json).decodeFromJsonObject(jsonObject)
    }

    override fun serialize(encoder: Encoder, value: ButtonData) {
        val jsonObject = ButtonDataEncoder(json).encodeToJsonObject(value)
        JsonObject.serializer().serialize(encoder, jsonObject)
    }
}
