package com.flipperdevices.archive.search.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.search.R
import com.flipperdevices.archive.search.model.SearchState
import com.flipperdevices.archive.shared.composable.ComposableKeyCard
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.keyparser.api.model.FlipperKeyParsed
import kotlinx.collections.immutable.ImmutableList
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableSearchContent(
    synchronizationUiApi: SynchronizationUiApi,
    state: SearchState,
    synchronizationState: SynchronizationState,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    when (state) {
        SearchState.Loading -> CategoryLoadingProgress(modifier)
        is SearchState.Loaded -> if (state.keys.isEmpty()) {
            CategoryEmpty(modifier)
        } else {
            CategoryList(
                modifier = modifier,
                onOpenKeyScreen = onOpenKeyScreen,
                synchronizationUiApi = synchronizationUiApi,
                synchronizationState = synchronizationState,
                keys = state.keys
            )
        }
    }
}

@Composable
private fun CategoryList(
    synchronizationUiApi: SynchronizationUiApi,
    synchronizationState: SynchronizationState,
    keys: ImmutableList<Pair<FlipperKeyParsed, FlipperKey>>,
    modifier: Modifier = Modifier,
    onOpenKeyScreen: (FlipperKeyPath) -> Unit
) {
    LazyColumn(
        modifier.padding(top = 14.dp)
    ) {
        items(keys) { (flipperKeyParsed, flipperKey) ->
            ComposableKeyCard(
                modifier = Modifier.padding(bottom = 14.dp),
                synchronizationContent = {
                    synchronizationUiApi.RenderSynchronizationState(
                        flipperKey.synchronized,
                        synchronizationState,
                        withText = false
                    )
                },
                flipperKeyParsed = flipperKeyParsed,
                onCardClick = {
                    onOpenKeyScreen(flipperKey.getKeyPath())
                }
            )
        }
    }
}

@Composable
private fun CategoryLoadingProgress(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CategoryEmpty(modifier: Modifier = Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(DesignSystem.drawable.ic_not_found),
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 12.dp),
            text = stringResource(R.string.search_not_found_title),
            style = LocalTypography.current.buttonM16
        )
        Text(
            modifier = Modifier.padding(top = 12.dp, start = 98.dp, end = 98.dp),
            text = stringResource(R.string.search_not_found_description),
            style = LocalTypography.current.bodyR16,
            color = LocalPallet.current.text40,
            textAlign = TextAlign.Center
        )
    }
}
