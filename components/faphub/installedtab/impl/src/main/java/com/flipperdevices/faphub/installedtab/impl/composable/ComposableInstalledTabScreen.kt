package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.installedtab.impl.R
import com.flipperdevices.faphub.installedtab.impl.composable.button.ComposableUpdateAllButton
import com.flipperdevices.faphub.installedtab.impl.composable.common.ComposableLoadingItemDivider
import com.flipperdevices.faphub.installedtab.impl.composable.offline.ComposableFapOfflineScreen
import com.flipperdevices.faphub.installedtab.impl.composable.online.ComposableOnlineFapApp
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledInternalState
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.model.OfflineFapApp
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import tangle.viewmodel.compose.tangleViewModel

private const val DEFAULT_FAP_COUNT = 20

@Composable
fun ComposableInstalledTabScreen(
    onOpenFapItem: (String) -> Unit,
    uninstallButtonOffline: @Composable (OfflineFapApp, Modifier) -> Unit,
    uninstallButtonOnline: @Composable (FapItemShort, Modifier) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<InstalledFapsViewModel>()
    val state by viewModel.getFapInstalledScreenState().collectAsState()
    val buttonState by remember {
        viewModel.getFapBatchUpdateButtonState()
    }.collectAsState()

    val screenModifier = modifier.padding(horizontal = 14.dp)

    when (val stateLocal = state) {
        is FapInstalledScreenState.Error -> errorsRenderer.ComposableThrowableError(
            throwable = stateLocal.throwable,
            onRetry = { viewModel.refresh(true) },
            modifier = screenModifier
                .fillMaxSize(),
            fapErrorSize = FapErrorSize.FULLSCREEN
        )

        is FapInstalledScreenState.Loaded,
        FapInstalledScreenState.Loading,
        is FapInstalledScreenState.LoadedOffline -> {
            SwipeRefresh(
                modifier = screenModifier,
                onRefresh = { viewModel.refresh(true) }
            ) {
                LazyColumn(modifier = it) {
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
                        installationButton = installationButton,
                        uninstallButtonOffline = uninstallButtonOffline,
                        uninstallButtonOnline = uninstallButtonOnline
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming", "LongMethod")
private fun LazyListScope.ComposableInstalledTabScreenState(
    screenState: FapInstalledScreenState,
    onOpenFapItem: (String) -> Unit,
    uninstallButtonOffline: @Composable (OfflineFapApp, Modifier) -> Unit,
    uninstallButtonOnline: @Composable (FapItemShort, Modifier) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    when (screenState) {
        is FapInstalledScreenState.Error -> {}
        is FapInstalledScreenState.LoadedOffline -> if (screenState.faps.isEmpty()) {
            item {
                ComposableEmpty(Modifier.fillParentMaxSize())
            }
        } else {
            ComposableFapOfflineScreen(
                offlineApps = screenState.faps,
                onOpen = onOpenFapItem,
                uninstallButton = uninstallButtonOffline
            )
        }

        FapInstalledScreenState.Loading -> items(DEFAULT_FAP_COUNT) {
            ComposableOnlineFapApp(
                modifier = Modifier.padding(vertical = 12.dp),
                fapItem = null,
                installationButton = { modifier ->
                    installationButton(null, modifier)
                },
                uninstallButton = { Box(it.placeholderConnecting()) }
            )
        }

        is FapInstalledScreenState.Loaded -> if (screenState.faps.isEmpty()) {
            item {
                ComposableEmpty(Modifier.fillParentMaxSize())
            }
        } else {
            items(
                count = screenState.faps.size,
                key = { screenState.faps[it].first.id }
            ) { index ->
                val (item, state) = screenState.faps[index]
                ComposableOnlineFapApp(
                    modifier = Modifier
                        .clickable(
                            onClick = { onOpenFapItem(item.id) }
                        )
                        .padding(vertical = 12.dp)
                        .animateItemPlacement(),
                    fapItem = item,
                    installationButton = { modifier ->
                        installationButton(item, modifier)
                    },
                    uninstallButton = {
                        when (state) {
                            FapInstalledInternalState.Installed,
                            is FapInstalledInternalState.ReadyToUpdate ->
                                uninstallButtonOnline(item, it)

                            FapInstalledInternalState.InstallingInProgress,
                            FapInstalledInternalState.InstallingInProgressActive,
                            FapInstalledInternalState.UpdatingInProgress,
                            FapInstalledInternalState.UpdatingInProgressActive -> {
                            }
                        }
                    }
                )
                if (index != screenState.faps.lastIndex) {
                    ComposableLoadingItemDivider()
                }
            }
        }
    }
}

@Composable
private fun ComposableEmpty(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.faphub_installed_empty_desc),
            color = LocalPallet.current.text40,
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyR14
        )
    }
}
