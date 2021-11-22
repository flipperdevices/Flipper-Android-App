package com.flipperdevices.connection.impl.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.connection.impl.composable.helper.rotatableSweepGradient

private val CORNER_RADIUS = 14.dp
private const val PROGRESS_BAR_END_PERCENT = 0.2f
private const val PROGRESS_COLOR = 0xFF3ADEB7

@Composable
fun ComposableConnectionBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val rotationTransition = rememberInfiniteTransition()
    val angelAnimated by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    val colorStops = listOf(
        0f to Color(PROGRESS_COLOR),
        PROGRESS_BAR_END_PERCENT to Color.White
    ).toTypedArray()
    Box(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .clip(RoundedCornerShape(CORNER_RADIUS))
            .background(
                brush = rotatableSweepGradient(
                    colorStops = colorStops,
                    angel = angelAnimated
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(all = 2.dp)
                .clip(RoundedCornerShape(CORNER_RADIUS))
                .background(Color.White)
        ) {
            Box(modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp)) {
                content()
            }
        }
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun PreviewComposableConnectionBackground() {
    ComposableConnectionBackground {
        Text(text = "Sample")
    }
}
