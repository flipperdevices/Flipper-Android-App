package com.flipperdevices.core.ui.ktx.elements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlin.math.roundToInt

private const val DOTS_COUNT = 3f
private const val DOTS_DURATION_MS = 3000

@Composable
fun animatedDots(): String {
    val infiniteTransition = rememberInfiniteTransition()
    val dotsCount by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = DOTS_COUNT,
        animationSpec = infiniteRepeatable(
            animation = tween(DOTS_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    return ".".repeat(dotsCount.roundToInt())
}
