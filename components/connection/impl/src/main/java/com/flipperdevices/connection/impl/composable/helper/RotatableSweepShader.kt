package com.flipperdevices.connection.impl.composable.helper

import android.graphics.Matrix
import android.graphics.SweepGradient
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

class RotatableSweepShader(
    centerX: Float,
    centerY: Float,
    colorsInt: IntArray,
    colorsSteps: FloatArray?
) : SweepGradient(centerX, centerY, colorsInt, colorsSteps) {
    private var gradientMatrix = Matrix()

    init {
        setLocalMatrix(gradientMatrix)
    }

    constructor(
        center: Offset,
        colors: List<Color>,
        colorStops: List<Float>?
    ) : this(
        center.x,
        center.y,
        colors.toIntArray(),
        colorStops?.toFloatArray()
    )

    fun rotate(angel: Float) {
        gradientMatrix.postRotate(angel)
        setLocalMatrix(gradientMatrix)
    }
}

private fun List<Color>.toIntArray(): IntArray =
    IntArray(size) { i -> this[i].toArgb() }
