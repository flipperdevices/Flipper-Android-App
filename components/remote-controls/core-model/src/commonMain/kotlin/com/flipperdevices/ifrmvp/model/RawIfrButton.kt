package com.flipperdevices.ifrmvp.model

import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * [RawIfrButton] is required to parse [data] as [JsonObject] so we can later
 * convert it to different [ButtonData]
 */
@Serializable
data class RawIfrButton(
    @SerialName("data")
    val data: JsonObject,
    @SerialName("position")
    val position: IfrButton.Position,
)
