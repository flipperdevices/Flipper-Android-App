package com.flipperdevices.main.impl.composable.switch

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.tab.tabIndicatorOffset
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.main.impl.model.FapHubTabEnum

@Composable
fun ComposableFapHubSwitch(
    fapHubTabEnum: FapHubTabEnum,
    onSelect: (FapHubTabEnum) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(LocalPallet.current.fapHubSwitchBackground)
    ) {
        ComposableFapHubSwitchInternal(fapHubTabEnum, onSelect)
    }
}


@Composable
private fun ComposableFapHubSwitchInternal(
    fapHubTabEnum: FapHubTabEnum,
    onSelect: (FapHubTabEnum) -> Unit
) {
    val tabs = FapHubTabEnum.values()
    var tabPositions by remember {
        mutableStateOf(emptyList<TabPosition>())
    }
    var tabHeightPx by remember { mutableStateOf(0) }
    if (tabPositions.size > fapHubTabEnum.ordinal) {
        val currentTabPosition = tabPositions[fapHubTabEnum.ordinal]
        val tabHeight = with(LocalDensity.current) { tabHeightPx.toDp() }
        ComposeTabAnimatedBackground(currentTabPosition, tabHeight)
    }
    TabRow(
        modifier = Modifier.onGloballyPositioned {
            tabHeightPx = it.size.height
        },
        selectedTabIndex = fapHubTabEnum.ordinal,
        backgroundColor = Color.Transparent,
        indicator = { tabPositions = it },
        // remove bottom divider from tabRow
        divider = { }
    ) {
        tabs.forEach {
            ComposableFapHubTab(
                it, onSelect,
                isSelected = fapHubTabEnum.ordinal == it.ordinal
            )
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
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .fillMaxHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 8.dp))
                .background(LocalPallet.current.fapHubSelectedBackgroundColor)
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableFapHubSwitchPreview() {
    FlipperThemeInternal() {
        var selectedTab by remember { mutableStateOf(FapHubTabEnum.APPS) }
        Box {
            ComposableFapHubSwitch(selectedTab, onSelect = {
                selectedTab = it
            })
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun ComposableFapHubSwitchDarkPreview() {
    FlipperThemeInternal(isLight = false) {
        var selectedTab by remember { mutableStateOf(FapHubTabEnum.APPS) }
        Box {
            ComposableFapHubSwitch(selectedTab, onSelect = {
                selectedTab = it
            })
        }
    }
}