package com.flipperdevices.archive.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.archive.impl.composable.page.ComposablePager
import com.flipperdevices.archive.impl.composable.tab.ComposableTabs
import com.flipperdevices.archive.impl.model.ArchiveTab
import com.flipperdevices.bridge.dao.api.model.FlipperFileType
import com.google.accompanist.pager.rememberPagerState

@Preview(
    showSystemUi = true,
    showBackground = true
)
@Composable
fun ComposableArchive(connectionBar: @Composable () -> Unit = {}) {
    val pagerState = rememberPagerState()

    val tabs = listOf(ArchiveTab(null)).plus(
        FlipperFileType.values().map { ArchiveTab(it) }
    )

    Scaffold(topBar = {
        connectionBar()
    }) {
        Column(modifier = Modifier.padding(it)) {
            ComposableTabs(pagerState, tabs)
            ComposablePager(
                modifier = Modifier.weight(1f),
                pagerState = pagerState,
                tabs = tabs
            )
        }
    }
}
