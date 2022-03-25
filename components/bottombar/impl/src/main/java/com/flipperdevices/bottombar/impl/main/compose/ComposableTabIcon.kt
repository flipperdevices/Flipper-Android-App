package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.composable.painterResourceByKey

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

    Icon(
        modifier = modifier.fillMaxSize(),
        painter = painterResourceByKey(
            if (selected) tabState.selectedIcon
            else tabState.notSelectedIcon
        ),
        contentDescription = description,
        tint = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
    )
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
