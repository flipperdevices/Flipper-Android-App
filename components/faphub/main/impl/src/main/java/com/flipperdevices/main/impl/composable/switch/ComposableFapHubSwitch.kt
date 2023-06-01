package com.flipperdevices.main.impl.composable.switch

import com.flipperdevices.core.ui.res.R as DesignSystem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.clickableRipple
import com.flipperdevices.core.ui.ktx.tab.tabIndicatorOffset
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.main.impl.model.FapHubTabEnum

@Composable
fun ComposableFapHubNewSwitch(
    fapHubTabEnum: FapHubTabEnum,
    onSelect: (FapHubTabEnum) -> Unit,
    installedNotificationCount: Int,
    onEndClick: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalPallet.current.accent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Image(
                modifier = Modifier
                    .padding(top = 11.dp, bottom = 11.dp, start = 16.dp, end = 2.dp)
                    .size(20.dp)
                    .clickableRipple(onClick = onBack),
                painter = painterResource(com.flipperdevices.core.ui.res.R.drawable.ic_back),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 18.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.5f))
        ) {
            CompositionLocalProvider(
                LocalPallet provides LocalPallet.current.copy(
                    fapHubActiveColor = Color.Black,
                    fapHubInactiveColor = Color.Black.copy(alpha = 0.5f)
                )
            ) {
                ComposableFapHubSwitchInternal(
                    fapHubTabEnum,
                    onSelect,
                    installedNotificationCount,
                    LocalPallet.current.accent
                )
            }
        }
        Icon(
            modifier = Modifier
                .padding(end = 14.dp)
                .size(24.dp)
                .clickableRipple(onClick = onEndClick),
            painter = painterResource(DesignSystem.drawable.ic_search),
            contentDescription = null,
            tint = LocalPallet.current.onAppBar
        )
    }
}

@Composable
private fun ComposableFapHubSwitchInternal(
    fapHubTabEnum: FapHubTabEnum,
    onSelect: (FapHubTabEnum) -> Unit,
    installedNotificationCount: Int,
    activeColor: Color
) {
    val tabs = FapHubTabEnum.values()
    var tabPositions by remember {
        mutableStateOf(emptyList<TabPosition>())
    }
    var tabHeightPx by remember { mutableStateOf(0) }
    if (tabPositions.size > fapHubTabEnum.ordinal) {
        val currentTabPosition = tabPositions[fapHubTabEnum.ordinal]
        val tabHeight = with(LocalDensity.current) { tabHeightPx.toDp() }
        ComposeTabAnimatedBackground(currentTabPosition, tabHeight, activeColor)
    }
    TabRow(
        modifier = Modifier
            .onGloballyPositioned {
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
                hubTabEnum = it,
                onSelectFapHubTabEnum = onSelect,
                isSelected = fapHubTabEnum.ordinal == it.ordinal,
                notificationCount = if (it == FapHubTabEnum.INSTALLED) {
                    installedNotificationCount
                } else {
                    0
                }
            )
        }
    }
}

@Composable
private fun ComposeTabAnimatedBackground(
    tabPosition: TabPosition,
    tabHeight: Dp,
    activeColor: Color
) {
    Box(
        modifier = Modifier
            .tabIndicatorOffset(tabPosition)
            .height(tabHeight)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp, vertical = 2.dp)
                .fillMaxHeight()
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 8.dp))
                .background(activeColor)
        )
    }
}