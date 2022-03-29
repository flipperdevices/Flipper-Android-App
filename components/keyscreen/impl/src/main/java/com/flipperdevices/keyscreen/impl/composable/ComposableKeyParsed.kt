package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableDelete
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableShare
import com.flipperdevices.keyscreen.impl.composable.card.ComposableKeyCard
import com.flipperdevices.keyscreen.impl.model.DeleteState
import com.flipperdevices.keyscreen.impl.model.KeyScreenState
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.shared.bar.ComposableBarCancelIcon
import com.flipperdevices.keyscreen.shared.bar.ComposableBarTitle
import com.flipperdevices.keyscreen.shared.bar.ComposableKeyScreenAppBar

@Composable
fun ComposableKeyParsed(
    viewModel: KeyScreenViewModel,
    keyScreenState: KeyScreenState.Ready,
    synchronizationUiApi: SynchronizationUiApi,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(onBack)
        ComposableKeyCard(
            keyScreenState.parsedKey,
            synchronizationState = {
                synchronizationUiApi.RenderSynchronizationState(
                    keyScreenState.flipperKey.path,
                    withText = true
                )
            },
            keyScreenState.favoriteState,
            viewModel::setFavorite,
        )
        ComposableEdit(viewModel::onOpenEdit)
        ComposableShare(keyScreenState.shareState, viewModel::onShare)
        if (keyScreenState.deleteState == DeleteState.DELETED) {
            onBack()
        } else ComposableDelete(keyScreenState.deleteState, viewModel::onDelete)
    }
}

@Composable
private fun ComposableKeyScreenBar(onBack: () -> Unit) {
    ComposableKeyScreenAppBar(
        centerBlock = {
            ComposableBarTitle(modifier = it, textId = R.string.keyscreen_title)
        },
        endBlock = {
            ComposableBarCancelIcon(it, onBack)
        }
    )
}
