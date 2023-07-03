package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.errors.ComposableThrowableError
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.faphub.appcard.composable.AppCard
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installedtab.impl.composable.button.ComposableUpdateAllButton
import com.flipperdevices.faphub.installedtab.impl.composable.common.ComposableLoadingItemDivider
import com.flipperdevices.faphub.installedtab.impl.composable.offline.ComposableFapOfflineScreen
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import tangle.viewmodel.compose.tangleViewModel

private const val DEFAULT_FAP_COUNT = 20

@Composable
fun ComposableInstalledTabScreen(
    onOpenFapItem: (String) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<InstalledFapsViewModel>()
    val state by viewModel.getFapInstalledScreenState().collectAsState()
    val buttonState by remember {
        viewModel.getFapBatchUpdateButtonState()
    }.collectAsState()

    val screenModifier = modifier.padding(horizontal = 14.dp)

    when (val stateLocal = state) {
        is FapInstalledScreenState.Error -> ComposableThrowableError(
            throwable = stateLocal.throwable,
            onRetry = viewModel::refresh,
            modifier = screenModifier
                .fillMaxSize()
        )

        is FapInstalledScreenState.Loaded,
        FapInstalledScreenState.Loading,
        is FapInstalledScreenState.LoadedOffline -> SwipeRefresh(onRefresh = viewModel::refresh) {
            LazyColumn(
                modifier = screenModifier
            ) {
                item {
                    ComposableUpdateAllButton(
                        state = buttonState,
                        onUpdateAll = viewModel::updateAll,
                        onCancelAll = viewModel::cancelAll,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
                ComposableInstalledTabScreenState(
                    screenState = stateLocal,
                    onOpenFapItem = onOpenFapItem,
                    installationButton = installationButton
                )
            }
        }
    }
}

@Suppress("FunctionNaming")
private fun LazyListScope.ComposableInstalledTabScreenState(
    screenState: FapInstalledScreenState,
    onOpenFapItem: (String) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    when (screenState) {
        is FapInstalledScreenState.Error -> {}
        is FapInstalledScreenState.LoadedOffline -> ComposableFapOfflineScreen(
            offlineApps = screenState.faps,
            onOpen = onOpenFapItem
        )

        FapInstalledScreenState.Loading -> items(DEFAULT_FAP_COUNT) {
            AppCard(
                modifier = Modifier.padding(vertical = 12.dp),
                fapItem = null,
                installationButton = { modifier ->
                    installationButton(null, modifier)
                }
            )
        }

        is FapInstalledScreenState.Loaded -> items(
            count = screenState.faps.size,
            key = { screenState.faps[it].id }
        ) { index ->
            val item = screenState.faps[index]
            AppCard(
                modifier = Modifier
                    .clickable(
                        onClick = { onOpenFapItem(item.id) }
                    )
                    .padding(vertical = 12.dp)
                    .animateItemPlacement(),
                fapItem = item,
                installationButton = { modifier ->
                    installationButton(item, modifier)
                }
            )
            if (index != screenState.faps.lastIndex) {
                ComposableLoadingItemDivider()
            }
        }
    }
}
