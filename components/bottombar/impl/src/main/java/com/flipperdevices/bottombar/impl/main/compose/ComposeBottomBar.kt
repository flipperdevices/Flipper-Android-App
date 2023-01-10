package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.flipperdevices.bottombar.impl.main.viewmodel.BottomBarViewModel
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.flipperdevices.connection.api.ConnectionApi
import com.flipperdevices.core.ui.ktx.tab.tabIndicatorOffset
import com.flipperdevices.core.ui.theme.LocalPallet
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposeBottomBar(
    connectionApi: ConnectionApi,
    modifier: Modifier = Modifier,
    selectedItem: FlipperBottomTab = FlipperBottomTab.ARCHIVE,
    onBottomBarClick: (FlipperBottomTab) -> Unit = {}
) {
    val tabs = remember { FlipperBottomTab.values() }
    var selectedIndex by remember {
        mutableStateOf(tabs.indexOf(selectedItem))
    }
    var tabPositions by remember {
        mutableStateOf(emptyList<TabPosition>())
    }
    var tabHeightPx by remember { mutableStateOf(0) }
    val bottomBarViewModel: BottomBarViewModel = tangleViewModel()
    val hubHasNotification by bottomBarViewModel.hasNotificationHubState().collectAsState()
    Box(
        modifier = modifier.background(LocalPallet.current.bottomBarBackground)
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
                    getTabStateFromFlipperBottomTab(
                        connectionApi,
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
    connectionApi.CheckAndShowUnsupportedDialog()
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
