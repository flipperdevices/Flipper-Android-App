package com.flipperdevices.infrared.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.infrared.compose.components.ComposableInfraredAppBar
import com.flipperdevices.infrared.viewmodel.InfraredViewModel
import com.flipperdevices.keyscreen.api.KeyEmulateApi
import com.flipperdevices.keyscreen.api.state.FavoriteState
import com.flipperdevices.keyscreen.api.state.KeyScreenState

@Composable
internal fun ComposableInfraredSuccessScreen(
    state: KeyScreenState.Ready,
    viewModel: InfraredViewModel,
    keyEmulateApi: KeyEmulateApi,
    onShare: () -> Unit,
    onBack: () -> Unit,
    onRename: (FlipperKeyPath) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ComposableInfraredAppBar(
            name = state.parsedKey.keyName,
            isFavorite = state.favoriteState == FavoriteState.FAVORITE,
            onShare = onShare,
            onBack = onBack,
            onEdit = {},
            onDelete = { viewModel.onDelete(onBack) },
            onRename = { viewModel.onRename(onRename) },
            onFavorite = viewModel::onFavorite,
        )
        keyEmulateApi.ComposableEmulateButton(modifier = Modifier, flipperKey = state.flipperKey)
    }
}
