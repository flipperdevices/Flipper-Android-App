package com.flipperdevices.core.ui.ktx.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeRefresh(
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(false, onRefresh)
    Box(modifier.clipToBounds().pullRefresh(pullRefreshState)) {
        content()
        PullRefreshIndicator(false, pullRefreshState, Modifier.align(Alignment.TopCenter))
    }
}
