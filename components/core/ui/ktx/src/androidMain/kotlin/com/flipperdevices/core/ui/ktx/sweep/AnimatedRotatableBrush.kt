package com.flipperdevices.core.ui.ktx.sweep

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

private const val PROGRESS_BAR_END_PERCENT_INFINITE = 0.2f
private const val PROGRESS_BAR_END_DURATION_MS = 1000

@Composable
fun animatedRotatableBrush(
    backgroundColor: Color,
    cursorColor: Color,
    tag: String,
    durationMillis: Int = PROGRESS_BAR_END_DURATION_MS,
    endPercent: Float = PROGRESS_BAR_END_PERCENT_INFINITE,
): Brush {
    val rotationTransition = rememberInfiniteTransition(label = tag)
    val angelAnimated by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = tag
    )
    return rotatableSweepGradient(
        0f to backgroundColor,
        endPercent to cursorColor,
        endPercent * 2 to backgroundColor,
        angel = angelAnimated
    )
}

fun rotatableSweepGradient(
    vararg colorStops: Pair<Float, Color>,
    angel: Float = 0f,
): RotatableSweepGradient = RotatableSweepGradient(
    colors = List(colorStops.size) { i -> colorStops[i].second },
    stops = List(colorStops.size) { i -> colorStops[i].first },
    center = Offset.Unspecified,
    angel = angel
)
