package com.flipperdevices.infrared.compose.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.infrared.compose.components.ComposableInfraredAppBar
import com.flipperdevices.infrared.compose.components.ComposableInfraredControls
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
    onEdit: () -> Unit,
    onBack: () -> Unit,
    onRename: (FlipperKeyPath) -> Unit
) {
    val controls by viewModel.controlState().collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ComposableInfraredAppBar(
            name = state.parsedKey.keyName,
            isFavorite = state.favoriteState == FavoriteState.FAVORITE,
            onShare = onShare,
            onBack = onBack,
            onEdit = onEdit,
            onDelete = { viewModel.onDelete(onBack) },
            onRename = { viewModel.onRename(onRename) },
            onFavorite = viewModel::onFavorite,
        )
        ComposableInfraredControls(
            controls = controls,
            flipperKey = state.flipperKey,
            keyEmulateApi = keyEmulateApi
        )
    }
}
