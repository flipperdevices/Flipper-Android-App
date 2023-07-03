package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.errors.ComposableThrowableError
import com.flipperdevices.faphub.appcard.composable.AppCard
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installedtab.impl.composable.button.ComposableUpdateAllButton
import com.flipperdevices.faphub.installedtab.impl.composable.common.ComposableLoadingItemDivider
import com.flipperdevices.faphub.installedtab.impl.composable.offline.ComposableFapOfflineScreen
import com.flipperdevices.faphub.installedtab.impl.model.FapBatchUpdateButtonState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import kotlinx.collections.immutable.ImmutableList
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

        is FapInstalledScreenState.Loaded -> ComposableInstalledTabScreen(
            faps = stateLocal.faps,
            buttonState = buttonState,
            onUpdateAll = viewModel::updateAll,
            onCancelAll = viewModel::cancelAll,
            onOpenFapItem = onOpenFapItem,
            installationButton = installationButton,
            modifier = screenModifier
        )

        FapInstalledScreenState.Loading -> ComposableInstalledTabScreen(
            faps = null,
            buttonState = buttonState,
            onUpdateAll = viewModel::updateAll,
            onCancelAll = viewModel::cancelAll,
            onOpenFapItem = onOpenFapItem,
            installationButton = installationButton,
            modifier = screenModifier
        )

        is FapInstalledScreenState.LoadedOffline -> ComposableFapOfflineScreen(
            offlineApps = stateLocal.faps,
            modifier = screenModifier,
            onOpen = onOpenFapItem
        )
    }
}

@Composable
private fun ComposableInstalledTabScreen(
    faps: ImmutableList<FapItemShort>?,
    onOpenFapItem: (String) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit,
    buttonState: FapBatchUpdateButtonState,
    onUpdateAll: () -> Unit,
    onCancelAll: () -> Unit,
    modifier: Modifier = Modifier
) = LazyColumn(modifier) {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            ComposableUpdateAllButton(
                state = buttonState,
                onUpdateAll = onUpdateAll,
                onCancelAll = onCancelAll
            )
        }
    }
    if (faps == null) {
        items(DEFAULT_FAP_COUNT) {
            AppCard(
                modifier = Modifier.padding(vertical = 12.dp),
                fapItem = null,
                installationButton = { modifier ->
                    installationButton(null, modifier)
                }
            )
        }
    } else {
        items(
            count = faps.size,
            key = { faps[it].id }
        ) { index ->
            val item = faps[index]
            AppCard(
                modifier = Modifier
                    .clickable(
                        onClick = { onOpenFapItem(item.id) }
                    )
                    .padding(vertical = 12.dp),
                fapItem = item,
                installationButton = { modifier ->
                    installationButton(item, modifier)
                }
            )
            if (index != faps.lastIndex) {
                ComposableLoadingItemDivider()
            }
        }
    }
}
