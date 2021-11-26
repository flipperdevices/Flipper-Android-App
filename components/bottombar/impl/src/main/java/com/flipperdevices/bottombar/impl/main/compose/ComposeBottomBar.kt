package com.flipperdevices.bottombar.impl.main.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.Text
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flipperdevices.bottombar.impl.R
import com.flipperdevices.bottombar.impl.model.FlipperBottomTab
import com.google.accompanist.pager.ExperimentalPagerApi

@OptIn(ExperimentalPagerApi::class)
@Preview(
    showBackground = true
)
@Composable
fun ComposeBottomBar(
    selectedItem: FlipperBottomTab = FlipperBottomTab.STORAGE,
    onBottomBarClick: (FlipperBottomTab) -> Unit = {}
) {
    val tabs = FlipperBottomTab.values()
    var selectedIndex by remember {
        mutableStateOf(tabs.indexOf(selectedItem))
    }
    var tabPositions by remember {
        mutableStateOf(emptyList<TabPosition>())
    }
    var tabHeightPx by remember {
        mutableStateOf(0)
    }

    Box(
        modifier = Modifier.background(Color.White)
    ) {
        if (tabPositions.size > selectedIndex) {
            val currentTabPosition = tabPositions[selectedIndex]
            val tabHeight = with(LocalDensity.current) { tabHeightPx.toDp() }
            Box(
                modifier = Modifier
                    .height(tabHeight)
                    .width(currentTabPosition.width)
                    .offset(currentTabPosition.left)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 2.dp)
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(size = 10.dp))
                        .background(colorResource(R.color.tab_active_color))
                )
            }
        }
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    tabHeightPx = it.size.height
                },
            backgroundColor = Color.Transparent,
            selectedTabIndex = selectedIndex,
            contentColor = colorResource(android.R.color.black),
            indicator = { tabPositions = it }
        ) {
            tabs.forEachIndexed { index, flipperBottomTab ->
                ComposeMaterialYouTab(
                    text = { Text(text = stringResource(flipperBottomTab.title)) },
                    icon = {
                        Icon(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(flipperBottomTab.icon),
                            contentDescription = stringResource(flipperBottomTab.title)
                        )
                    },
                    selected = selectedIndex == index,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = colorResource(android.R.color.darker_gray),
                    onClick = {
                        selectedIndex = index
                    }
                )
            }
        }
    }
}
