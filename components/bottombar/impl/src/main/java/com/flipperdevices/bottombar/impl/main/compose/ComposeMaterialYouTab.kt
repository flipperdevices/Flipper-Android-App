package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bottombar.impl.R

@Suppress("LongParameterList")
@Composable
fun ComposeMaterialYouTab(
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor.copy(alpha = ContentAlpha.medium)
) {
    TabTransition(
        activeColor = selectedContentColor,
        inactiveColor = unselectedContentColor,
        selected = selected
    ) {
        Box(
            Modifier.clickable(
                onClick = { onClick?.invoke() }
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.padding(vertical = 9.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .size(size = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    icon?.invoke()
                }
                Box() {
                    text?.invoke()
                }
            }
        }
    }
}

// Tab transition specifications
private const val TAB_FADE_IN_ANIMATION_DURATION = 150
private const val TAB_FADE_IN_ANIMATION_DELAY = 100
private const val TAB_FADE_OUT_ANIMATION_DURATION = 100

@Composable
private fun TabTransition(
    activeColor: Color,
    inactiveColor: Color,
    selected: Boolean,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(selected)
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
        }, label = "colorChangeAnimation"
    ) {
        if (it) activeColor else inactiveColor
    }
    CompositionLocalProvider(
        LocalContentColor provides color.copy(alpha = 1f),
        LocalContentAlpha provides color.alpha,
        content = content
    )
}

@Preview
@Composable
fun ComposeMaterialYouTabPreview() {
    ComposeMaterialYouTab(
        text = { Text(text = "test") },
        icon = { Icon(painterResource(R.drawable.ic_device), contentDescription = null) }
    )
}
