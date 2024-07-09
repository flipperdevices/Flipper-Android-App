package com.flipperdevices.remotecontrols.impl.brands.composable.composable.alphabet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet

@Composable
internal fun <T> AlphabetSearchComposable(
    items: ImmutableList<T>,
    toHeader: (T) -> Char,
    headers: ImmutableSet<Char>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val scrollState = remember(items, headers) {
        ScrollState(
            items = items,
            headers = headers,
            toHeader = toHeader
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        content()

        LaunchedEffect(listState, scrollState.isScrollingToIndex) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .collect { scrollState.onScrolled(listState) }
        }
        HeadersComposable(
            scrollState = scrollState,
            listState = listState,
            headers = headers
        )
    }
}
