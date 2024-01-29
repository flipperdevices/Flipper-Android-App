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
    hubHasNotification: Boolean,
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
            hubHasNotification = hubHasNotification
        )
    }
}

@Composable
private fun ComposeBottomBarInternal(
    connectionTabState: TabState,
    selectedItem: BottomBarTabEnum,
    hubHasNotification: Boolean,
    onBottomBarClick: (BottomBarTabEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = remember { BottomBarTabEnum.entries.toTypedArray() }
    var selectedIndex by remember(selectedItem) {
        mutableIntStateOf(tabs.indexOf(selectedItem))
    }
    var tabPositions by remember {
        mutableStateOf(emptyList<TabPosition>())
    }
    var tabHeightPx by remember { mutableStateOf(0) }
    Box(
        modifier = modifier.background(LocalPallet.current.bottomBarBackground)
            .navigationBarsPadding()
    ) {
        if (tabPositions.size > selectedIndex) {
            val currentTabPosition = tabPositions[selectedIndex]
            val tabHeight = with(LocalDensity.current) { tabHeightPx.toDp() }
            ComposeTabAnimatedBackground(currentTabPosition, tabHeight)
        }
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    tabHeightPx = it.size.height
                },
            backgroundColor = Color.Transparent,
            selectedTabIndex = selectedIndex,
            contentColor = LocalPallet.current.bottomBarContent,
            indicator = { tabPositions = it },
            // remove bottom divider from tabRow
            divider = { }
        ) {
            tabs.forEachIndexed { index, flipperBottomTab ->
                ComposeMaterialYouTab(
                    tabState = getTabStateFromFlipperBottomTab(
                        connectionTabState,
                        flipperBottomTab,
                        hubHasNotification
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

@Composable
private fun ComposeTabAnimatedBackground(
    tabPosition: TabPosition,
    tabHeight: Dp
) {
    Box(
        modifier = Modifier
            .tabIndicatorOffset(tabPosition)
            .height(tabHeight)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 2.dp)
                .fillMaxHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 10.dp))
                .background(LocalPallet.current.bottomBarTabBackground)
        )
    }
}
