package com.flipperdevices.wearable.sync.wear.impl.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.wearable.sync.wear.impl.R
import com.flipperdevices.wearable.sync.wear.impl.model.FlipperWearKey
import com.flipperdevices.wearable.sync.wear.impl.model.KeysListState
import com.flipperdevices.wearable.sync.wear.impl.viewmodel.KeysListViewModel
import com.google.android.horologist.compose.layout.fillMaxRectangle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableKeysList(
    onKeyOpen: (FlipperWearKey) -> Unit = {},
    keysListViewModel: KeysListViewModel = viewModel()
) {
    val state by keysListViewModel.getKeysListFlow().collectAsState()
    val localState = state
    when (localState) {
        is KeysListState.Loaded -> if (localState.keys.isEmpty()) {
            ComposableKeysListEmpty()
        } else {
            ComposableKeysListInternal(localState.keys, onKeyOpen)
        }
        KeysListState.Loading -> ComposableKeysListLoading()
    }
}

@Composable
private fun ComposableKeysListLoading() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview(
    showSystemUi = true,
    showBackground = true,
    device = Devices.WEAR_OS_LARGE_ROUND
)
private fun ComposableKeysListEmpty() {
    Column(
        Modifier.fillMaxRectangle(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(DesignSystem.drawable.ic_not_found),
            contentDescription = stringResource(R.string.keys_not_found)
        )
        Text(
            text = stringResource(R.string.keys_not_found),
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyM14
        )
    }
}

@Composable
private fun ComposableKeysListInternal(
    keys: ImmutableList<FlipperWearKey>,
    onKeyOpen: (FlipperWearKey) -> Unit
) {
    val columnScrollState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val favoritesKeys = remember(keys) { keys.filter { it.isFavorites }.sortedBy { it.path.path } }
    val otherKeys = remember(keys) { keys.filterNot { it.isFavorites }.sortedBy { it.path.path } }
    ScalingLazyColumn(
        modifier = Modifier
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    columnScrollState.scrollBy(it.verticalScrollPixels)
                }
                true
            }
            .focusRequester(focusRequester)
            .focusable(),
        state = columnScrollState,
        horizontalAlignment = Alignment.Start
    ) {
        if (favoritesKeys.isNotEmpty()) {
            item {
                ComposableKeysListCategory(
                    textId = R.string.keys_category_favorites
                )
            }
            items(favoritesKeys) {
                ComposableKeysListElement(it) { onKeyOpen(it) }
            }
            item {
                ComposableKeysListCategory(
                    textId = R.string.keys_category_others
                )
            }
        }
        items(otherKeys) {
            ComposableKeysListElement(it) { onKeyOpen(it) }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
