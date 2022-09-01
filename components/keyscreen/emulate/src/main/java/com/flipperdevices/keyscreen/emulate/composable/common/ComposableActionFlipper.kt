package com.flipperdevices.keyscreen.emulate.composable.common

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor

@Composable
@Suppress("LongParameterList")
fun ComposableActionFlipper(
    modifier: Modifier,
    color: Color,
    @StringRes textId: Int,
    @DrawableRes iconId: Int,
    @RawRes animId: Int,
    isAction: Boolean,
    content: @Composable () -> Unit
) {
    val translateAnimation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    val animColor = if (isAction) Brush.horizontalGradient(
        colorStops = arrayOf(
            0f to color.copy(alpha = 0.4f),
            translateAnimation to color.copy(alpha = 0.9f),
            1.0f to color.copy(alpha = 0.4f)
        )
    ) else SolidColor(color)

    ComposableEmulateButton(
        modifier = modifier,
        buttonModifier = Modifier,
        buttonContent = {
            ComposableActionFlipperContent(
                iconId = iconId,
                textId = textId,
                animId = if (isAction) animId else null
            )
        },
        underButtonContent = content,
        borderColor = color,
        backgroundBrush = animColor
    )
}
