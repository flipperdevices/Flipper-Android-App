package com.flipperdevices.bottombar.impl.composable.bottombar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.ktx.image.painterResourceByKey

private const val FULL_CIRCLE = 360
private const val TIME_ANIM = 1000

@Composable
fun ComposableTabIcon(tabState: TabState, selected: Boolean) {
    when (tabState) {
        is TabState.Static -> ComposableTabIconStatic(tabState, selected)
        is TabState.Animated -> ComposableTabIconAnimated(tabState, selected)
    }
}

@Composable
private fun ComposableTabIconStatic(
    tabState: TabState.Static,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier.fillMaxSize(),
        painter = painterResourceByKey(
            if (selected) {
                tabState.selectedIcon
            } else {
                tabState.notSelectedIcon
            }
        ),
        contentDescription = tabState.text,
        tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    )
}

@Composable
private fun ComposableTabIconAnimated(
    tabState: TabState.Animated,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val iconId = if (selected) tabState.selectedIcon else tabState.notSelectedIcon
    val backgroundId = if (selected) tabState.selectedBackground else tabState.notSelectedBackground

    val value by rememberInfiniteTransition().animateFloat(
        0.0f,
        1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = TIME_ANIM,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundId),
            contentDescription = null
        )
        Image(
            modifier = Modifier.rotate(degrees = value * FULL_CIRCLE),
            painter = painterResource(id = iconId),
            contentDescription = null
        )
    }
}
