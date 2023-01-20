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
import com.flipperdevices.archive.shared.composable.ComposableAppBar
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ktx.jre.roundPercentToString
import com.flipperdevices.core.ui.ktx.LocalRouter
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.widget.screen.R
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun WidgetOptionsComposable(
    archiveApi: ArchiveApi,
    widgetSelectViewModel: WidgetSelectViewModel
) {
    val synchronizationState by widgetSelectViewModel.getSynchronizationFlow().collectAsState()
    val localSynchronizationState = synchronizationState
    if (localSynchronizationState is SynchronizationState.InProgress) {
        ArchiveProgressScreen(localSynchronizationState)
    } else {
        ComposableArchiveReady(
            archiveApi,
            widgetSelectViewModel
        )
    }
}

@Composable
private fun ComposableArchiveReady(
    archiveApi: ArchiveApi,
    widgetSelectViewModel: WidgetSelectViewModel
) {
    val router = LocalRouter.current
    Column(verticalArrangement = Arrangement.Top) {
        ComposableAppBar(
            title = stringResource(R.string.widget_options_title),
            iconId = DesignSystem.drawable.ic_search,
            onIconClick = { widgetSelectViewModel.onOpenSearch(router) }
        )
        ComposableKeys(archiveApi, widgetSelectViewModel)
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
