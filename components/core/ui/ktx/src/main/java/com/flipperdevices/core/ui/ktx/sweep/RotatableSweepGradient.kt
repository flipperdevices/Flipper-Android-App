package com.flipperdevices.core.ui.ktx.sweep

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush

private const val HASH_MAGIC_NUMBER = 31

class RotatableSweepGradient(
    private val center: Offset,
    private val colors: List<Color>,
    private val stops: List<Float>? = null,
    private val angel: Float
) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        return RotatableSweepShader(
            if (center.isUnspecified) {
                size.center
            } else {
                Offset(
                    if (center.x == Float.POSITIVE_INFINITY) size.width else center.x,
                    if (center.y == Float.POSITIVE_INFINITY) size.height else center.y
                )
            },
            colors,
            stops,
            angel
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RotatableSweepGradient) return false

        if (center != other.center) return false
        if (colors != other.colors) return false
        if (stops != other.stops) return false
        if (angel != other.angel) return false

        return true
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = HASH_MAGIC_NUMBER * result + colors.hashCode()
        result = HASH_MAGIC_NUMBER * result + (stops?.hashCode() ?: 0)
        result = HASH_MAGIC_NUMBER * result + angel.hashCode()
        return result
    }

    override fun toString(): String {
        val centerValue = if (center.isSpecified) "center=$center, " else ""
        return "RotatableSweepGradient(" +
            centerValue +
            "colors=$colors, stops=$stops)"
    }
}
