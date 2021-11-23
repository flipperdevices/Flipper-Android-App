package com.flipperdevices.archive.impl.composable.tab.helper

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.max

class RectWithCornerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val arcSize = size.height
        val rectSize = Size(max(0f, size.width - arcSize * 2), size.height)

        val path = Path().apply {
            addArc(
                Rect(
                    Offset.Zero,
                    Size(arcSize * 2, arcSize * 2)
                ),
                startAngleDegrees = 180f,
                sweepAngleDegrees = 90f
            )
            moveTo(0f, size.height)
            lineTo(size.height, size.height)
            lineTo(size.height, 0f)
            addRect(
                Rect(
                    Offset(arcSize, 0f),
                    rectSize
                )
            )
            moveTo(arcSize + rectSize.width, 0f)
            lineTo(arcSize + rectSize.width, size.height)
            lineTo(arcSize * 2 + rectSize.width, size.height)
            addArc(
                Rect(
                    Offset(rectSize.width, 0f),
                    Size(arcSize * 2, arcSize * 2)
                ),
                startAngleDegrees = 270f,
                sweepAngleDegrees = 90f
            )
        }
        return Outline.Generic(path)
    }
}
