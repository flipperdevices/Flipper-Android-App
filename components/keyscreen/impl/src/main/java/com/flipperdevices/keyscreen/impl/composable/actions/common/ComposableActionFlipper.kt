package com.flipperdevices.keyscreen.impl.composable.actions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun ComposableActionFlipper(
    modifier: Modifier,
    color: Color,
    @StringRes textId: Int,
    @DrawableRes iconId: Int,
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

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .height(49.dp)
                .fillMaxWidth()
                .background(animColor)
                .border(
                    width = 2.dp,
                    color = color,
                    shape = RoundedCornerShape(12.dp)
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComposableActionFlipperContent(
                iconId = iconId,
                textId = textId
            )
        }
        content()
    }
}
