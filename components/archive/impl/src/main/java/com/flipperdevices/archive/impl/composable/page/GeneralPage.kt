package com.flipperdevices.archive.impl.composable.page

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.archive.impl.R
import com.flipperdevices.archive.impl.composable.category.ComposableCategoryCard
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.core.ui.R as DesignSystem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun GeneralPage(
    tabViewModel: GeneralTabViewModel = viewModel()
) {
    val keys by tabViewModel.getKeys().collectAsState()
    val favoriteKeys by tabViewModel.getFavoriteKeys().collectAsState()
    val synchronizationState by tabViewModel.getSynchronizationState().collectAsState()
    val isKeysPresented = !favoriteKeys.isNullOrEmpty() || !keys.isNullOrEmpty()

    Column(verticalArrangement = Arrangement.Top) {
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
                    KeyCatalog(favoriteKeys, keys)
                }
            }
        }

        if (!isKeysPresented) {
            if (synchronizationState == SynchronizationState.IN_PROGRESS) {
                ComposableProgress()
            } else ComposableNoKeys()
        }
    }
}

@Suppress("FunctionName")
private fun LazyListScope.KeyCatalog(
    favoriteKeys: List<FlipperKey>,
    otherKeys: List<FlipperKey>?
) {
    if (!favoriteKeys.isNullOrEmpty()) {
        item {
            ComposableFavoriteKeysTitle()
        }
        ComposableKeysGrid(favoriteKeys)
    }

    if (!otherKeys.isNullOrEmpty()) {
        item {
            ComposableAllKeysTitle()
        }
        ComposableKeysGrid(otherKeys)
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
            fontWeight = FontWeight.W400,
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_40)
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
            painter = painterResource(R.drawable.ic_progress),
            tint = colorResource(DesignSystem.color.accent_secondary),
            contentDescription = stringResource(R.string.archive_sync_progress)
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.archive_sync_progress),
            fontSize = 16.sp,
            color = colorResource(DesignSystem.color.black_40)
        )
    }
}
