package com.flipperdevices.bottombar.impl.composable.bottombar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.bottombar.impl.model.BottomBarTabEnum
import com.flipperdevices.bottombar.model.TabState
import com.flipperdevices.core.ui.ktx.tab.tabIndicatorOffset
import com.flipperdevices.core.ui.theme.LocalPallet

@Composable
fun ComposeBottomBar(
    connectionTabState: TabState,
    selectedItem: BottomBarTabEnum,
    toolsHasNotification: Boolean,
    appsHasNotification: Boolean,
    onBottomBarClick: (BottomBarTabEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = !WindowInsets.isImeVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ComposeBottomBarInternal(
            connectionTabState = connectionTabState,
            modifier = modifier,
            selectedItem = selectedItem,
            onBottomBarClick = onBottomBarClick,
            toolsHasNotification = toolsHasNotification,
            appsHasNotification = appsHasNotification
        )
    }
}

@Composable
private fun ComposeBottomBarInternal(
    connectionTabState: TabState,
    selectedItem: BottomBarTabEnum,
    toolsHasNotification: Boolean,
    appsHasNotification: Boolean,
    onBottomBarClick: (BottomBarTabEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { BottomBarTabEnum.entries.toTypedArray() }
    var selectedIndex by remember(selectedItem) {
        mutableIntStateOf(tabs.indexOf(selectedItem))
    }
    Box(
        modifier = modifier
            .background(LocalPallet.current.bottomBarBackground)
            .navigationBarsPadding()
    ) {
        TabRow(
            modifier = Modifier
                .fillMaxWidth(),
            backgroundColor = Color.Transparent,
            selectedTabIndex = selectedIndex,
            contentColor = LocalPallet.current.bottomBarContent,
            indicator = { },
            // remove bottom divider from tabRow
            divider = { }
        ) {
            tabs.forEachIndexed { index, flipperBottomTab ->
                ComposeMaterialYouTab(
                    tabState = getTabStateFromFlipperBottomTab(
                        connectionTabState,
                        flipperBottomTab,
                        appsHasNotification = appsHasNotification,
                        toolsHasNotification = toolsHasNotification
                    ),
                    selected = selectedIndex == index,
                    onClick = {
                        selectedIndex = index
                        onBottomBarClick(tabs[index])
                    }
                )
            }
        }
    }
}
