package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.flipperdevices.core.ui.errors.ComposableThrowableError
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.AppCard
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import kotlinx.collections.immutable.ImmutableList
import tangle.viewmodel.compose.tangleViewModel

private const val DEFAULT_FAP_COUNT = 20

@Composable
fun ComposableInstalledTabScreen(
    onOpenFapItem: (FapItemShort) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier, TextUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = tangleViewModel<InstalledFapsViewModel>()
    val state by viewModel.getFapInstalledScreenState().collectAsState()

    state.let { stateLocal ->
        when (stateLocal) {
            is FapInstalledScreenState.Error -> ComposableThrowableError(
                throwable = stateLocal.throwable,
                onRetry = viewModel::onRefresh,
                modifier = modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp)
            )

            is FapInstalledScreenState.Loaded -> ComposableInstalledTabScreen(
                faps = stateLocal.faps,
                onOpenFapItem = onOpenFapItem,
                installationButton = installationButton,
                modifier = modifier
            )

            FapInstalledScreenState.Loading -> ComposableInstalledTabScreen(
                faps = null,
                onOpenFapItem = onOpenFapItem,
                installationButton = installationButton,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ComposableInstalledTabScreen(
    faps: ImmutableList<FapItemShort>?,
    onOpenFapItem: (FapItemShort) -> Unit,
    installationButton: @Composable (FapItemShort?, Modifier, TextUnit) -> Unit,
    modifier: Modifier = Modifier
) = LazyColumn(modifier) {
    item {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            ComposableUpdateAllButton(
                if (faps == null) {
                    Modifier.placeholderConnecting()
                } else {
                    Modifier
                }
            )
        }
    }
    if (faps == null) {
        items(DEFAULT_FAP_COUNT) {
            AppCard(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                fapItem = null,
                installationButton = { modifier, fontSize ->
                    installationButton(null, modifier, fontSize)
                }
            )
        }
    } else {
        items(faps.size) { index ->
            val item = faps[index]
            AppCard(
                modifier = Modifier
                    .clickable(
                        onClick = { onOpenFapItem(item) }
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                fapItem = item,
                installationButton = { modifier, fontSize ->
                    installationButton(item, modifier, fontSize)
                }
            )
            if (index != faps.lastIndex) {
                ComposableLoadingItemDivider()
            }
        }
    }
}

@Composable
private fun ComposableLoadingItemDivider() = Divider(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp),
    thickness = 1.dp,
    color = LocalPallet.current.fapHubDividerColor
)
