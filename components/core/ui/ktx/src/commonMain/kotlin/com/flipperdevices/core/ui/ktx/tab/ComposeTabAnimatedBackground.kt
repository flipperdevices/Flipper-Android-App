package com.flipperdevices.core.ui.ktx.tab

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.TabPosition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo

private const val ANIMATION_WIDTH_CHANGE_DURATION_MS = 250
private const val ANIMATION_OFFSET_CHANGE_DURATION_MS = 150

@Suppress("ModifierComposed") // MOB-1039
fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(
            durationMillis = ANIMATION_WIDTH_CHANGE_DURATION_MS,
            easing = FastOutSlowInEasing
        )
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(
            durationMillis = ANIMATION_OFFSET_CHANGE_DURATION_MS,
            easing = FastOutSlowInEasing
        )
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}
