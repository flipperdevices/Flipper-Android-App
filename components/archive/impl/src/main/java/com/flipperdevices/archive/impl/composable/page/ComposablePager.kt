package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.impl.composable.key.ComposableFlipperKey
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.bridge.dao.FlipperKey
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
        state = pagerState,
    ) { page ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp),
            verticalArrangement = Arrangement.spacedBy(space = 12.dp)
        ) {
            items(dummyKeys.size) {
                ComposableFlipperKey(dummyKeys[it])
            }
        }
    }
}
