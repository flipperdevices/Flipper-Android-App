package com.flipperdevices.faphub.installedtab.impl.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.flipperdevices.core.ui.ktx.placeholderConnecting
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.faphub.appcard.composable.AppCard
import com.flipperdevices.faphub.dao.api.model.FapItem
import com.flipperdevices.faphub.installedtab.impl.model.FapInstalledScreenState
import com.flipperdevices.faphub.installedtab.impl.viewmodel.InstalledFapsViewModel
import tangle.viewmodel.compose.tangleViewModel

private const val DEFAULT_FAP_COUNT = 20

@Composable
fun ComposableInstalledTabScreen(
    onOpenFapItem: (FapItem) -> Unit,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) {
    val viewModel = tangleViewModel<InstalledFapsViewModel>()
    val state by viewModel.getFapInstalledScreenState().collectAsState()
    val faps = (state as? FapInstalledScreenState.Loaded)?.faps

    ComposableInstalledTabScreen(
        faps = faps,
        onOpenFapItem = onOpenFapItem,
        installationButton = installationButton
    )
}

@Composable
private fun ComposableInstalledTabScreen(
    faps: List<FapItem>?,
    onOpenFapItem: (FapItem) -> Unit,
    installationButton: @Composable (FapItem?, Modifier, TextUnit) -> Unit
) {
    LazyColumn {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                ComposableUpdateAllButton(
                    if (faps == null) {
                        Modifier.placeholderConnecting()
                    } else Modifier
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
        } else items(faps.size) { index ->
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