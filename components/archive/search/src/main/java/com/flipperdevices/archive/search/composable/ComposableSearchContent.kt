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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.search.R
import com.flipperdevices.archive.search.model.SearchState
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.archive.shared.composable.ComposableKeyCard
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.parsed.FlipperKeyParsed
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.core.ui.res.R as DesignSystem
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography

@Composable
fun ComposableSearchContent(
    modifier: Modifier,
    synchronizationUiApi: SynchronizationUiApi,
    searchViewModel: SearchViewModel
) {
    val state by searchViewModel.getState().collectAsState()
    val synchronizationState by searchViewModel.getSynchronizationState().collectAsState()
    val localState = state
    when (localState) {
        SearchState.Loading -> CategoryLoadingProgress(modifier)
        is SearchState.Loaded -> if (localState.keys.isEmpty()) {
            CategoryEmpty(modifier)
        } else CategoryList(
            modifier,
            searchViewModel,
            synchronizationUiApi,
            synchronizationState,
            localState.keys
        )
    }
}

@Composable
private fun CategoryList(
    modifier: Modifier,
    searchViewModel: SearchViewModel,
    synchronizationUiApi: SynchronizationUiApi,
    synchronizationState: SynchronizationState,
    keys: List<Pair<FlipperKeyParsed, FlipperKey>>
) {
    val router = LocalRouter.current
    LazyColumn(
        modifier.padding(top = 14.dp)
    ) {
        items(keys) { (flipperKeyParsed, flipperKey) ->
            ComposableKeyCard(
                Modifier.padding(bottom = 14.dp),
                synchronizationContent = {
                    synchronizationUiApi.RenderSynchronizationState(
                        flipperKey.synchronized,
                        synchronizationState,
                        withText = false
                    )
                },
                flipperKeyParsed
            ) {
                searchViewModel.openKeyScreen(router, flipperKey.path)
            }
        }
    }
}

@Composable
private fun CategoryLoadingProgress(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CategoryEmpty(modifier: Modifier) {
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
            style = LocalTypography.current.buttonM16,
            color = LocalPallet.current.text100
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
