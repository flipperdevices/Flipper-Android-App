package com.flipperdevices.archive.impl.composable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.category.ComposableCategoryCard
import com.flipperdevices.archive.impl.composable.page.ArchiveProgressScreen
import com.flipperdevices.archive.impl.composable.page.ComposableAllKeysTitle
import com.flipperdevices.archive.impl.composable.page.ComposableFavoriteKeysTitle
import com.flipperdevices.archive.impl.composable.page.ComposableKeysGrid
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.archive.impl.viewmodel.KeyItemViewModel
import com.flipperdevices.archive.shared.composable.ComposableAppBar
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.core.ui.theme.LocalTypography
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import tangle.viewmodel.compose.tangleViewModel
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
fun ComposableArchive(
    synchronizationUiApi: SynchronizationUiApi,
    tabViewModel: GeneralTabViewModel = tangleViewModel()
) {
    val keys by tabViewModel.getKeys().collectAsState()
    val favoriteKeys by tabViewModel.getFavoriteKeys().collectAsState()
    val synchronizationState by tabViewModel.getSynchronizationState().collectAsState()
    val localSynchronizationState = synchronizationState
    val isKeysPresented = favoriteKeys.isNotEmpty() || !keys.isNullOrEmpty()

    if (localSynchronizationState is SynchronizationState.InProgress) {
        ArchiveProgressScreen(localSynchronizationState, tabViewModel::cancelSynchronization)
    } else {
        ComposableArchiveReady(
            synchronizationUiApi,
            keys,
            favoriteKeys,
            tabViewModel,
            synchronizationState,
            isKeysPresented
        )
    }
}

@Composable
private fun ComposableArchiveReady(
    synchronizationUiApi: SynchronizationUiApi,
    keys: List<FlipperKey>?,
    favoriteKeys: List<FlipperKey>,
    tabViewModel: GeneralTabViewModel,
    synchronizationState: SynchronizationState,
    isKeysPresented: Boolean
) {
    val keyItemViewModel: KeyItemViewModel = viewModel()
    Column(verticalArrangement = Arrangement.Top) {
        ComposableAppBar(
            title = stringResource(R.string.archive_title),
            iconId = DesignSystem.drawable.ic_search,
            onIconClick = { tabViewModel.onOpenSearch() }
        )
        SwipeRefresh(
            state = rememberSwipeRefreshState(false),
            onRefresh = tabViewModel::refresh
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    ComposableCategoryCard()
                }
                if (isKeysPresented) {
                    KeyCatalog(
                        favoriteKeys = favoriteKeys,
                        otherKeys = keys,
                        synchronizationUiApi = synchronizationUiApi,
                        synchronizationState = synchronizationState,
                        onKeyOpen = keyItemViewModel::open
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
}

@Suppress("FunctionName")
private fun LazyListScope.KeyCatalog(
    favoriteKeys: List<FlipperKey>,
    otherKeys: List<FlipperKey>?,
    synchronizationUiApi: SynchronizationUiApi,
    synchronizationState: SynchronizationState,
    onKeyOpen: (FlipperKeyPath) -> Unit
) {
    if (favoriteKeys.isNotEmpty()) {
        item {
            ComposableFavoriteKeysTitle()
        }
        ComposableKeysGrid(
            favoriteKeys,
            synchronizationUiApi,
            synchronizationState,
            onKeyOpen
        )
    }

    if (!otherKeys.isNullOrEmpty()) {
        item {
            ComposableAllKeysTitle()
        }
        ComposableKeysGrid(
            otherKeys,
            synchronizationUiApi,
            synchronizationState,
            onKeyOpen
        )
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
            text = stringResource(R.string.archive_content_empty),
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
            painter = painterResource(DesignSystem.drawable.ic_progress),
            tint = LocalPallet.current.accentSecond,
            contentDescription = stringResource(R.string.archive_sync_progress)
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.archive_sync_progress),
            style = LocalTypography.current.bodyR16,
            color = LocalPallet.current.text40
        )
    }
}
