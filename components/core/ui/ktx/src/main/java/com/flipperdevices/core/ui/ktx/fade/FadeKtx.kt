package com.flipperdevices.core.ui.ktx.fade

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.theme.FlipperThemeInternal

fun Modifier.fadingEdge(orientation: FadeOrientation): Modifier = composed(
    factory = {
        val fillColor = Color.White
        val brush = remember { orientation.toBrush(fillColor) }
        this.fadingEdge(brush)
    }
)

private fun FadeOrientation.toBrush(fillColor: Color) = when (this) {
    is FadeOrientation.Bottom -> {
        Brush.verticalGradient(
            0f to fillColor,
            this.THRESHOLD to fillColor,
            1f to Color.Transparent
        )
    }

    is FadeOrientation.Top -> {
        Brush.verticalGradient(
            0f to Color.Transparent,
            this.THRESHOLD to fillColor
        )
    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun FadePreviewColumn(
    itemsAmount: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        repeat(itemsAmount) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color.Red)
            )
        }
    }
}

@Preview
@Composable
private fun TopFadePreview() {
    FlipperThemeInternal {
        FadePreviewColumn(
            modifier = Modifier.fadingEdge(FadeOrientation.Top),
            itemsAmount = 4
        )
    }
}

@Preview
@Composable
private fun BottomFadePreview() {
    FlipperThemeInternal {
        FadePreviewColumn(
            modifier = Modifier.fadingEdge(FadeOrientation.Bottom),
            itemsAmount = 4
        )
    }
}
