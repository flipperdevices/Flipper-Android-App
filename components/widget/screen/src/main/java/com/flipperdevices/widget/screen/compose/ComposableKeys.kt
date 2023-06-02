package com.flipperdevices.widget.screen.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flipperdevices.archive.api.ArchiveApi
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ui.ktx.elements.SwipeRefresh
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.flipperdevices.widget.screen.R
import com.flipperdevices.widget.screen.viewmodel.WidgetSelectViewModel

@Composable
fun ColumnScope.ComposableKeys(
    archiveApi: ArchiveApi,
    widgetSelectViewModel: WidgetSelectViewModel,
    modifier: Modifier = Modifier
) {
    val keys by widgetSelectViewModel.getKeysFlow().collectAsState()
    val favoriteKeys by widgetSelectViewModel.getFavoriteKeysFlow().collectAsState()
    val synchronizationState by widgetSelectViewModel.getSynchronizationFlow().collectAsState()
    val isKeysPresented = favoriteKeys.isNotEmpty() || keys.isNotEmpty()

    SwipeRefresh(onRefresh = widgetSelectViewModel::refresh) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
        ) {
            if (isKeysPresented) {
                KeyCatalog(
                    archiveApi = archiveApi,
                    favoriteKeys = favoriteKeys,
                    otherKeys = keys,
                    synchronizationState = synchronizationState,
                    onKeyOpen = widgetSelectViewModel::onSelectKey
                )
            }
        }
    }
    if (!isKeysPresented) {
        if (synchronizationState is SynchronizationState.InProgress) {
            ComposableProgress()
        } else {
            ComposableNoKeys()
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.KeyCatalog(
    archiveApi: ArchiveApi,
    favoriteKeys: List<FlipperKey>,
    otherKeys: List<FlipperKey>?,
    synchronizationState: SynchronizationState,
    onKeyOpen: (FlipperKeyPath) -> Unit
) {
    if (favoriteKeys.isNotEmpty()) {
        item {
            ComposableFavoriteKeysTitle()
        }
        with(archiveApi) {
            ComposableKeysGridWithSynchronization(
                favoriteKeys,
                synchronizationState,
                onKeyOpen
            )
        }
    }

    if (!otherKeys.isNullOrEmpty()) {
        item {
            ComposableAllKeysTitle()
        }
        with(archiveApi) {
            ComposableKeysGridWithSynchronization(
                otherKeys,
                synchronizationState,
                onKeyOpen
            )
        }
    }
}

@Composable
private fun ColumnScope.ComposableNoKeys() {
    Box(
        modifier = Modifier
            .weight(weight = 1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.widget_options_content_empty),
            style = LocalTypography.current.bodyR16,
            color = LocalPallet.current.text40
        )
    }
}

@Composable
private fun ColumnScope.ComposableProgress() {
    Column(
        modifier = Modifier
            .weight(weight = 1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val infiniteTransition = rememberInfiniteTransition()
        val angle by infiniteTransition.animateFloat(
            initialValue = 0F,
            targetValue = 360F,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing)
            )
        )
        Icon(
            modifier = Modifier.rotate(angle),
            painter = painterResource(com.flipperdevices.core.ui.res.R.drawable.ic_progress),
            tint = LocalPallet.current.accentSecond,
            contentDescription = stringResource(R.string.widget_options_sync_progress)
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.widget_options_sync_progress),
            style = LocalTypography.current.bodyR16,
            color = LocalPallet.current.text40
        )
    }
}

@Composable
fun ComposableFavoriteKeysTitle(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 24.dp, start = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.widget_options_favorite_title),
            style = LocalTypography.current.buttonB16
        )
        Icon(
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .size(size = 20.dp),
            painter = painterResource(com.flipperdevices.core.ui.res.R.drawable.ic_star_enabled),
            tint = LocalPallet.current.keyFavorite,
            contentDescription = null
        )
    }
}

@Composable
fun ComposableAllKeysTitle(
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(top = 24.dp, start = 14.dp),
        text = stringResource(R.string.widget_options_all_title),
        style = LocalTypography.current.buttonB16
    )
}
