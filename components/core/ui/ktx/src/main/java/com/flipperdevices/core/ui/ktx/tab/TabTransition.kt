package com.flipperdevices.core.ui.ktx.tab

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

// Tab transition specifications
private const val TAB_FADE_IN_ANIMATION_DURATION = 150
private const val TAB_FADE_IN_ANIMATION_DELAY = 100
private const val TAB_FADE_OUT_ANIMATION_DURATION = 100

@Composable
fun TabTransition(
    activeColor: Color,
    inactiveColor: Color,
    selected: Boolean,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(selected, label = "TabTransition")
    val color by transition.animateColor(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = TAB_FADE_IN_ANIMATION_DURATION,
                    delayMillis = TAB_FADE_IN_ANIMATION_DELAY,
                    easing = LinearEasing
                )
            } else {
                tween(
                    durationMillis = TAB_FADE_OUT_ANIMATION_DURATION,
                    easing = LinearEasing
                )
            }
        },
        label = "colorChangeAnimation"
    ) {
        if (it) activeColor else inactiveColor
    }

    CompositionLocalProvider(
        LocalContentColor provides color.copy(alpha = 1f),
        LocalContentAlpha provides color.alpha,
        content = content
    )
}
