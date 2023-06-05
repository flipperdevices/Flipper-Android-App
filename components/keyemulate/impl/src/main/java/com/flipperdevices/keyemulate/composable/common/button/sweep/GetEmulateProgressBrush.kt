package com.flipperdevices.keyemulate.composable.common.button.sweep

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import com.flipperdevices.core.ktx.jre.toIntSafe
import com.flipperdevices.keyemulate.model.EmulateProgress
import kotlin.math.max

private const val PROGRESS_BAR_END_PERCENT_FIXED_DELTA = 0.05f
private const val PROGRESS_BAR_END_PERCENT_INFINITE = 0.2f
private const val PROGRESS_BAR_END = 1.0f
private const val PROGRESS_BAR_END_DURATION_MS = 1000

@Composable
fun getEmulateProgressBrush(
    emulateProgress: EmulateProgress?,
    backgroundColor: Color,
    cursorColor: Color
): Brush {
    return when (emulateProgress) {
        null -> {
            SolidColor(cursorColor)
        }
        is EmulateProgress.Growing -> growingBrush(
            backgroundColor,
            cursorColor,
            emulateProgress.duration
        )
        is EmulateProgress.GrowingAndStop -> growingBrushOneTime(
            backgroundColor,
            cursorColor,
            emulateProgress.duration
        )
        EmulateProgress.Infinite -> animatedBrush(backgroundColor, cursorColor)
    }
}

private const val FIXED_SIZE_BRUSH_ANGEL = -90f

@Composable
private fun growingBrush(
    backgroundColor: Color,
    cursorColor: Color,
    duration: Long
): Brush {
    val fixedProgress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = PROGRESS_BAR_END,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration.toIntSafe(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    return rotatableSweepGradient(
        0f to cursorColor,
        max(0f, fixedProgress - PROGRESS_BAR_END_PERCENT_FIXED_DELTA) to cursorColor,
        fixedProgress to backgroundColor,
        angel = FIXED_SIZE_BRUSH_ANGEL
    )
}

@Composable
private fun growingBrushOneTime(
    backgroundColor: Color,
    cursorColor: Color,
    duration: Long
): Brush {
    val fixedProgressState = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fixedProgressState.animateTo(
            targetValue = PROGRESS_BAR_END,
            animationSpec = tween(
                durationMillis = duration.toIntSafe(),
                easing = LinearEasing
            )
        )
    }
    val fixedProgress = fixedProgressState.value

    if (fixedProgress == PROGRESS_BAR_END) {
        return animatedBrush(backgroundColor, cursorColor)
    }

    return rotatableSweepGradient(
        0f to cursorColor,
        max(0f, fixedProgress - PROGRESS_BAR_END_PERCENT_FIXED_DELTA) to cursorColor,
        fixedProgress to backgroundColor,
        angel = FIXED_SIZE_BRUSH_ANGEL
    )
}

@Composable
private fun animatedBrush(backgroundColor: Color, cursorColor: Color): Brush {
    val rotationTransition = rememberInfiniteTransition()
    val angelAnimated by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = PROGRESS_BAR_END_DURATION_MS,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )
    return rotatableSweepGradient(
        0f to backgroundColor,
        PROGRESS_BAR_END_PERCENT_INFINITE to cursorColor,
        PROGRESS_BAR_END_PERCENT_INFINITE * 2 to backgroundColor,
        angel = angelAnimated
    )
}
