package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState

@ExperimentalPagerApi
@Composable
fun ComposablePager(
    modifier: Modifier,
    pagerState: PagerState,
    tabs: List<ArchiveTab>
) {
    val dummyKeys = FlipperKey.DUMMY_LIST

    HorizontalPager(
        modifier = modifier
            .fillMaxWidth(),
        count = tabs.size,
        state = pagerState
    ) { page ->
        ArchivePage(tabs[page])
    }
}
