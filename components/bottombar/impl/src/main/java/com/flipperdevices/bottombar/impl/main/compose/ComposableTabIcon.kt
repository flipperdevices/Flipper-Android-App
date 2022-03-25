package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toolingGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flipperdevices.bottombar.model.TabState

@Composable
fun ComposableTabIcon(tabState: TabState, selected: Boolean) {
    when (tabState) {
        is TabState.Static -> ComposableTabIconStatic(Modifier, tabState, selected)
        is TabState.Animated -> ComposableTabIconAnimated(Modifier, tabState, selected)
    }
}

@Composable
private fun ComposableTabIconStatic(
    modifier: Modifier,
    tabState: TabState.Static,
    selected: Boolean
) {
    val description = stringResource(tabState.textId)
    val tintColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    val selectedIcon = if (selected) tabState.selectedIcon else tabState.notSelectedIcon

    key(selectedIcon, tintColor) {
        Box(
            Modifier
                .toolingGraphicsLayer()
                .paint(
                    painterResource(selectedIcon),
                    colorFilter = ColorFilter.tint(tintColor),
                    contentScale = ContentScale.Fit
                )
                .semantics {
                    this.contentDescription = description
                    this.role = Role.Image
                }
        )
    }
}

@Composable
private fun ComposableTabIconAnimated(
    modifier: Modifier,
    tabState: TabState.Animated,
    selected: Boolean
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            if (selected) tabState.selectedIcon
            else tabState.notSelectedIcon
        )
    )
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = progress
    )
}
