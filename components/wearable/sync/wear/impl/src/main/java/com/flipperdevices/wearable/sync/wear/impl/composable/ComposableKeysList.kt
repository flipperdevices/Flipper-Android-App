package com.flipperdevices.wearable.sync.wear.impl.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.wearable.core.ui.components.ComposableWearOsScalingLazyColumn
import com.flipperdevices.wearable.core.ui.components.ComposableWearOsScrollableColumn
import com.flipperdevices.wearable.sync.wear.impl.R
import com.flipperdevices.wearable.sync.wear.impl.model.FlipperWearKey
import com.flipperdevices.wearable.sync.wear.impl.model.KeysListState
import com.flipperdevices.wearable.sync.wear.impl.viewmodel.KeysListViewModel
import kotlinx.collections.immutable.ImmutableList
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableKeysList(
    onKeyOpen: (FlipperWearKey) -> Unit,
    keysListViewModel: KeysListViewModel
) {
    val state by keysListViewModel.getKeysListFlow().collectAsState()
    when (val localState = state) {
        is KeysListState.Loaded -> if (localState.keys.isEmpty()) {
            ComposableKeysListEmpty()
        } else {
            ComposableKeysListInternal(localState.keys, onKeyOpen)
        }

        KeysListState.Loading -> ComposableKeysListLoading()
        KeysListState.PhoneNotFound -> ComposableFindPhone(keysListViewModel::openStore)
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
private fun ComposableKeysListEmpty() {
    ComposableWearOsScrollableColumn {
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
    val favoritesKeys = remember(keys) { keys.filter { it.isFavorites }.sortedBy { it.path.path } }
    val otherKeys = remember(keys) { keys.filterNot { it.isFavorites }.sortedBy { it.path.path } }
    ComposableWearOsScalingLazyColumn {
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
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    device = WearDevices.LARGE_ROUND
)
@Composable
private fun PreviewComposableKeysListEmpty() {
    ComposableKeysListLoading()
}
