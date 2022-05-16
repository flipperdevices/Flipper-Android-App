package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.composable.LocalRouter
import com.flipperdevices.keyscreen.impl.R
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableDelete
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableEdit
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableEmulate
import com.flipperdevices.keyscreen.impl.composable.actions.ComposableRestore
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
    val router = LocalRouter.current
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ComposableKeyScreenBar(onBack)
        ComposableKeyCard(
            keyScreenState.parsedKey,
            keyScreenState.deleteState,
            synchronizationState = if (keyScreenState.deleteState == DeleteState.NOT_DELETED) { ->
                synchronizationUiApi.RenderSynchronizationState(
                    keyScreenState.flipperKey.path,
                    withText = true
                )
            } else null,
            keyScreenState.favoriteState,
            viewModel::setFavorite
        )

        val flipperAppName = keyScreenState.parsedKey.fileType?.flipperAppName

        if (flipperAppName != null) {
            ComposableEmulate(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                onClick = { viewModel.onEmulate(flipperAppName, keyScreenState.flipperKey.path) }
            )
        }

        if (keyScreenState.deleteState == DeleteState.NOT_DELETED) {
            ComposableEdit(viewModel::onOpenEdit)
            ComposableShare(keyScreenState.shareState, viewModel::onShare)
        } else if (keyScreenState.deleteState == DeleteState.DELETED) {
            ComposableRestore {
                viewModel.onRestore(router)
            }
        }

        ComposableDelete(keyScreenState.deleteState) {
            viewModel.onDelete(router)
        }
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
