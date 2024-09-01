package com.flipperdevices.core.ui.ktx.sweep

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class RotatableSweepShader(
    centerX: Float,
    centerY: Float,
    colorsInt: IntArray,
    colorsSteps: FloatArray?,
    angel: Float
) : SweepGradient(centerX, centerY, colorsInt, colorsSteps) {
    private val gradientMatrix = Matrix()

    init {
        gradientMatrix.postRotate(angel, centerX, centerY)
        setLocalMatrix(gradientMatrix)
    }

    constructor(
        center: Offset,
        colors: List<Color>,
        colorStops: List<Float>?,
        angel: Float
    ) : this(
        center.x,
        center.y,
        colors.toIntArray(),
        colorStops?.toFloatArray(),
        angel
    )
}

private fun List<Color>.toIntArray(): IntArray =
    IntArray(size) { i -> this[i].toArgb() }
