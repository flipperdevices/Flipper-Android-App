package com.flipperdevices.keyemulate.composable.common.button

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
import com.flipperdevices.core.ui.ktx.sweep.animatedRotatableBrush
import com.flipperdevices.core.ui.ktx.sweep.rotatableSweepGradient
import com.flipperdevices.keyemulate.model.EmulateProgress
import kotlin.math.max

private const val PROGRESS_BAR_END_PERCENT_FIXED_DELTA = 0.05f
private const val PROGRESS_BAR_END = 1.0f

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
        EmulateProgress.Infinite -> animatedRotatableBrush(backgroundColor, cursorColor, "Infinite")
    }
}

private const val FIXED_SIZE_BRUSH_ANGEL = -90f

@Composable
private fun growingBrush(
    backgroundColor: Color,
    cursorColor: Color,
    duration: Long
): Brush {
    val fixedProgress by rememberInfiniteTransition(label = "growingBrush").animateFloat(
        initialValue = 0f,
        targetValue = PROGRESS_BAR_END,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = duration.toIntSafe(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "growingBrush"
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
        return animatedRotatableBrush(backgroundColor, cursorColor, "growingBrushOneTime")
    }

    return rotatableSweepGradient(
        0f to cursorColor,
        max(0f, fixedProgress - PROGRESS_BAR_END_PERCENT_FIXED_DELTA) to cursorColor,
        fixedProgress to backgroundColor,
        angel = FIXED_SIZE_BRUSH_ANGEL
    )
}
