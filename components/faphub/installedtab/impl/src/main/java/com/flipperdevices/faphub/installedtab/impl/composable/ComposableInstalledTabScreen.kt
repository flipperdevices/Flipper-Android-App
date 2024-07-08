package com.flipperdevices.faphub.installedtab.impl.composable

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
import com.flipperdevices.faphub.errors.api.throwable.toFapHubError
import com.flipperdevices.faphub.installedtab.impl.R
import com.flipperdevices.faphub.installedtab.impl.composable.button.ComposableErrorButton
import com.flipperdevices.faphub.installedtab.impl.composable.button.ComposableUpdateAllButton
import com.flipperdevices.faphub.installedtab.impl.composable.common.ComposableLoadingItemDivider
import com.flipperdevices.faphub.installedtab.impl.composable.online.ComposableOnlineFapApp
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.model.InstalledFapApp
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel

private const val DEFAULT_FAP_COUNT = 20

@Composable
internal fun ComposableInstalledTabScreen(
    onOpenFapItem: (String) -> Unit,
    uninstallButtonOffline: @Composable (InstalledFapApp.OfflineFapApp, Modifier) -> Unit,
    uninstallButtonOnline: @Composable (FapItemShort, Modifier) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit,
    errorsRenderer: FapHubComposableErrorsRenderer,
    viewModel: InstalledFapsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.getFapInstalledScreenState().collectAsState()
    val buttonState by viewModel.getFapBatchUpdateButtonState().collectAsState()

    val screenModifier = modifier.padding(horizontal = 14.dp)

    when (val stateLocal = state) {
        is FapInstalledScreenState.Error -> errorsRenderer.ComposableThrowableError(
            throwable = stateLocal.throwable.toFapHubError(),
            onRetry = { viewModel.refresh(true) },
            modifier = screenModifier
                .fillMaxSize(),
            fapErrorSize = FapErrorSize.FULLSCREEN
        )

        is FapInstalledScreenState.Loaded -> SwipeRefresh(
            modifier = screenModifier,
            onRefresh = { viewModel.refresh(true) }
        ) {
            LazyColumn {
                if (stateLocal.networkError == null) {
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
                } else {
                    item {
                        ComposableErrorButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            installedNetworkErrorEnum = stateLocal.networkError
                        )
                    }
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

@Suppress("FunctionNaming", "LongMethod")
private fun LazyListScope.ComposableInstalledTabScreenState(
    screenState: FapInstalledScreenState,
    onOpenFapItem: (String) -> Unit,
    uninstallButtonOffline: @Composable (InstalledFapApp.OfflineFapApp, Modifier) -> Unit,
    uninstallButtonOnline: @Composable (FapItemShort, Modifier) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier) -> Unit
) {
    when (screenState) {
        is FapInstalledScreenState.Error -> {}
        is FapInstalledScreenState.Loaded -> if (screenState.faps.isEmpty()) {
            if (screenState.inProgress) {
                items(DEFAULT_FAP_COUNT) { _ ->
                    ComposableOnlineFapApp(
                        modifier = Modifier.padding(vertical = 12.dp),
                        fapItem = null,
                        installationButton = { modifier ->
                            installationButton(null, modifier)
                        },
                        uninstallButton = { modifier -> Box(modifier.placeholderConnecting()) }
                    )
                }
            } else {
                item {
                    ComposableEmpty(Modifier.fillParentMaxSize())
                }
            }
        } else {
            items(
                count = screenState.faps.size,
                key = { screenState.faps[it].first.applicationUid }
            ) { index ->
                val (item, state) = screenState.faps[index]
                ComposableFapApp(
                    installedFapApp = item,
                    state = state,
                    installationButton = installationButton,
                    uninstallButtonOffline = uninstallButtonOffline,
                    uninstallButtonOnline = uninstallButtonOnline,
                    onOpenFapItem = onOpenFapItem,
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                    // Waiting for fix https://issuetracker.google.com/issues/326278117
                    // .animateItemPlacement()
                )

                if (index != screenState.faps.lastIndex) {
                    ComposableLoadingItemDivider()
                }
            }

            if (screenState.inProgress) {
                item {
                    ComposableOnlineFapApp(
                        modifier = Modifier.padding(vertical = 12.dp),
                        fapItem = null,
                        installationButton = { modifier ->
                            installationButton(null, modifier)
                        },
                        uninstallButton = { Box(it.placeholderConnecting()) }
                    )
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
