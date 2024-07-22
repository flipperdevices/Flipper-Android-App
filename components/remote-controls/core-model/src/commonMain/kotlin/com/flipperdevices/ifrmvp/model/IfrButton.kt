package com.flipperdevices.ifrmvp.model

import com.flipperdevices.ifrmvp.model.buttondata.ButtonData
import com.flipperdevices.ifrmvp.model.serialization.ButtonDataSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IfrButton(
    @SerialName("data")
    @Serializable(ButtonDataSerializer::class)
    val data: ButtonData,
    @SerialName("position")
    val position: Position,
) {
    @Serializable
    data class Position(
        @SerialName("y")
        val y: Int,
        @SerialName("x")
        val x: Int,
        @SerialName("alignment")
        val alignment: Alignment = Alignment.CENTER,
        @SerialName("z_index")
        val zIndex: Float = 1f,
        @SerialName("container_width")
        val containerWidth: Int = 1,
        @SerialName("container_height")
        val containerHeight: Int = 1
    )

    @Serializable
    enum class Alignment {
        CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER_LEFT, CENTER_RIGHT
    }
}
