package com.flipperdevices.widget.screen.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.ktx.OrangeAppBarWithIcon
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.widget.screen.R
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel
import kotlinx.collections.immutable.ImmutableList
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun WidgetOptionsComposable(
    archiveApi: ArchiveApi,
    widgetSelectViewModel: WidgetSelectViewModel,
    onOpenSearchScreen: () -> Unit,
) {
    val keys by widgetSelectViewModel.getKeysFlow().collectAsState()
    val favoriteKeys by widgetSelectViewModel.getFavoriteKeysFlow().collectAsState()
    val synchronizationState by widgetSelectViewModel.getSynchronizationFlow().collectAsState()

    val localSynchronizationState = synchronizationState
    if (localSynchronizationState is SynchronizationState.InProgress) {
        ArchiveProgressScreen(localSynchronizationState)
    } else {
        ComposableArchiveReady(
            archiveApi = archiveApi,
            keys = keys,
            favoriteKeys = favoriteKeys,
            synchronizationState = localSynchronizationState,
            onRefresh = widgetSelectViewModel::refresh,
            onKeyOpen = widgetSelectViewModel::onSelectKey,
            onOpenSearchScreen = onOpenSearchScreen
        )
    }
}

@Composable
private fun ComposableArchiveReady(
    archiveApi: ArchiveApi,
    keys: ImmutableList<FlipperKey>,
    favoriteKeys: ImmutableList<FlipperKey>,
    synchronizationState: SynchronizationState,
    onRefresh: () -> Unit,
    onKeyOpen: (FlipperKeyPath) -> Unit,
    onOpenSearchScreen: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.Top) {
        OrangeAppBarWithIcon(
            title = stringResource(R.string.widget_options_title),
            endIconId = DesignSystem.drawable.ic_search,
            onEndClick = onOpenSearchScreen
        )
        ComposableKeys(
            archiveApi = archiveApi,
            keys = keys,
            favoriteKeys = favoriteKeys,
            synchronizationState = synchronizationState,
            onRefresh = onRefresh,
            onKeyOpen = onKeyOpen,
        )
    }
}

@Composable
private fun ArchiveProgressScreen(inProgressState: SynchronizationState.InProgress) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = LocalPallet.current.accentSecond,
            strokeWidth = 2.dp
        )
        Text(
            modifier = Modifier.padding(top = 14.dp),
            text = LocalContext.current.getString(
                R.string.widget_options_sync_percent,
                inProgressState.progress.roundPercentToString()
            ),
            style = LocalTypography.current.bodyM14,
            color = LocalPallet.current.text60,
            textAlign = TextAlign.Center
        )
    }
}
