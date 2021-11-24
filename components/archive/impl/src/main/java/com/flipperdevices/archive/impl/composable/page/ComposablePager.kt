package com.flipperdevices.archive.impl.composable.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.flipperdevices.archive.impl.model.ArchiveTab
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
    HorizontalPager(
        modifier = modifier
            .fillMaxWidth(),
        count = tabs.size,
        state = pagerState,
    ) { page ->
        // Our content for each page
        Card {
            Box(Modifier.fillMaxSize()) {
                Text(
                    text = "Page: ${tabs[page].name}",
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
