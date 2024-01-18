package com.flipperdevices.keyscreen.impl.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading

@Composable
@Suppress("NonSkippableComposable")
fun ComposableKeyScreen(
    viewModel: KeyScreenViewModel,
    synchronizationUiApi: SynchronizationUiApi,
    keyEmulateApi: KeyEmulateApi,
    componentContext: ComponentContext,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onOpenNfcEditor: (FlipperKeyPath) -> Unit,
    onOpenEditScreen: (FlipperKeyPath) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyScreenState by viewModel.getKeyScreenState().collectAsState()

    when (val localKeyScreenState = keyScreenState) {
        KeyScreenState.InProgress -> ComposableKeyScreenLoading(modifier)
        is KeyScreenState.Error -> ComposableKeyScreenError(
            modifier = modifier,
            text = stringResource(id = localKeyScreenState.reason)
        )

        is KeyScreenState.Ready -> ComposableKeyParsed(
            localKeyScreenState,
            synchronizationUiApi,
            keyEmulateApi,
            onShare = onShare,
            onBack = onBack,
            onOpenNfcEditor = onOpenNfcEditor,
            setFavorite = viewModel::setFavorite,
            onEdit = { viewModel.onOpenEdit(onOpenEditScreen) },
            emulateConfig = localKeyScreenState.emulateConfig,
            onRestore = { viewModel.onRestore(onBack) },
            onDelete = { viewModel.onDelete(onBack) },
            modifier = modifier,
            componentContext = componentContext
        )
    }
}
