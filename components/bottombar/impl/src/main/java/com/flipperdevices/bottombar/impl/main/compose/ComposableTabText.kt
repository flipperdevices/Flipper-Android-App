package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flipperdevices.bottombar.model.TabState
import kotlin.math.roundToInt

private const val DOTS_COUNT = 3f
private const val DOTS_DURATION_MS = 3000

@Composable
fun ComposableStatusText(
    tabState: TabState
) {
    val originalText = stringResource(tabState.textId)
    var animatedText = originalText
    if (tabState.textDotsAnimated) {
        val infiniteTransition = rememberInfiniteTransition()
        val dotsCount by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = DOTS_COUNT,
            animationSpec = infiniteRepeatable(
                animation = tween(DOTS_DURATION_MS, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        animatedText = originalText + ".".repeat(dotsCount.roundToInt())
    }

    Text(
        text = animatedText,
        fontSize = 10.sp,
        fontWeight = FontWeight.W700
    )
}
