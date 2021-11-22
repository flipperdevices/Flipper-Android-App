package com.flipperdevices.connection.impl.composable.helper

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
    private val stops: List<Float>? = null
) : ShaderBrush() {
    private var shader: RotatableSweepShader? = null
    private var rotationAngel = 0f
        set(value) {
            field = value
            shader?.rotate(value)
        }

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
            stops
        ).apply {
            shader = this
            rotate(rotationAngel)
        }
    }

    fun rotate(angel: Float) {
        rotationAngel = angel
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RotatableSweepGradient) return false

        if (center != other.center) return false
        if (colors != other.colors) return false
        if (stops != other.stops) return false

        return true
    }

    override fun hashCode(): Int {
        var result = center.hashCode()
        result = HASH_MAGIC_NUMBER * result + colors.hashCode()
        result = HASH_MAGIC_NUMBER * result + (stops?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        val centerValue = if (center.isSpecified) "center=$center, " else ""
        return "RotatableSweepGradient(" +
            centerValue +
            "colors=$colors, stops=$stops)"
    }
}

fun rotatableSweepGradient(
    vararg colorStops: Pair<Float, Color>,
    center: Offset = Offset.Unspecified
): RotatableSweepGradient = RotatableSweepGradient(
    colors = List(colorStops.size) { i -> colorStops[i].second },
    stops = List(colorStops.size) { i -> colorStops[i].first },
    center = center
)
