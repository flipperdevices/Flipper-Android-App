package com.flipperdevices.archive.impl.composable.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun ComposableTabs(pagerState: PagerState, tabs: List<ArchiveTab>) {
    val coroutineScope = rememberCoroutineScope()

    Column() {
        ScrollableTabRow(
            backgroundColor = Color.White,
            selectedTabIndex = pagerState.currentPage,
            contentColor = colorResource(R.color.tab_active_color),
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            // Add tabs for all of our pages
            tabs.forEachIndexed { index, tab ->
                Tab(
                    text = {
                        Text(
                            text = tab.fileType?.humanReadableName
                                ?: stringResource(R.string.archive_tab_all),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                    },
                    selected = pagerState.currentPage == index,
                    selectedContentColor = colorResource(R.color.tab_active_color),
                    unselectedContentColor = colorResource(R.color.tab_inactive_color),
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
    }
}
