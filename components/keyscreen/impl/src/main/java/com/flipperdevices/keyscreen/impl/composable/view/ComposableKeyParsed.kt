package com.flipperdevices.keyscreen.impl.composable.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.keyscreen.impl.composable.view.actions.ComposableEdit
import com.flipperdevices.keyscreen.impl.composable.view.actions.ComposableFavorite
import com.flipperdevices.keyscreen.impl.composable.view.actions.ComposableShare
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.viewmodel.view.KeyScreenViewModel

@Composable
fun ComposableKeyParsed(
    viewModel: KeyScreenViewModel,
    keyScreenState: KeyScreenState.Ready,
    onOpenEdit: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyCard(keyScreenState.parsedKey)
        ComposableEdit(onOpenEdit)
        ComposableFavorite(keyScreenState.favoriteState, viewModel::setFavorite)
        ComposableShare(keyScreenState.shareState, viewModel::onShare)
    }
}
