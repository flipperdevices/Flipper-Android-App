package com.flipperdevices.infrared.impl.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.infrared.api.InfraredConnectionApi.InfraredEmulateState
import com.flipperdevices.infrared.impl.R
import com.flipperdevices.infrared.impl.composable.components.ComposableIconText
import com.flipperdevices.infrared.impl.composable.components.ComposableInfraredAppBar
import com.flipperdevices.infrared.impl.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
@Suppress("NonSkippableComposable")
internal fun ComposableInfraredScreen(
    viewModel: InfraredViewModel,
    keyScreenApi: KeyScreenApi,
    keyEmulateApi: KeyEmulateApi,
    keyEmulateUiApi: KeyEmulateUiApi,
    componentContext: ComponentContext,
    onEdit: (FlipperKeyPath) -> Unit,
    onRename: (FlipperKeyPath) -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.getState().collectAsState()
    val emulateState by viewModel.getEmulateState().collectAsState()
    when (val localState = state) {
        is KeyScreenState.Error -> ComposableKeyScreenError(
            modifier = modifier,
            text = stringResource(id = localState.reason)
        )

        KeyScreenState.InProgress -> ComposableKeyScreenLoading(modifier)
        is KeyScreenState.Ready ->
            Column(modifier = modifier.fillMaxSize()) {
                ComposableInfraredAppBar(
                    onBack = onBack,
                    onEdit = { onEdit(localState.flipperKey.getKeyPath()) },
                    onDelete = { viewModel.onDelete(onEndAction = onBack) },
                    onShare = onShare,
                    keyName = localState.flipperKey.path.nameWithoutExtension,
                    emulatingInProgress = localState.emulatingInProgress
                )
                ComposableInfraredScreenReady(
                    state = localState,
                    emulateState = emulateState,
                    keyCardContent = {
                        keyScreenApi.KeyCard(
                            onEdit = { viewModel.onRename(onRename) },
                            onFavorite = viewModel::setFavorite,
                            state = localState,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(vertical = 14.dp),
                            componentContext = componentContext
                        )
                    },
                    keyEmulateContent = { config ->
                        keyEmulateApi.ComposableEmulateButton(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            emulateConfig = config,
                            isSynchronized = localState.flipperKey.synchronized,
                            componentContext = componentContext
                        )
                    },
                    keyEmulateErrorContent = {
                        ComposableEmulateError(emulateState)
                    },
                    keyEmulateSyncingContent = {
                        ComposableEmulateSyncing(keyEmulateUiApi)
                    }
                )
            }
    }
}

private const val PLACEHOLDER_BUTTONS_AMOUNT = 3

@Composable
private fun ComposableEmulateSyncing(
    keyEmulateUiApi: KeyEmulateUiApi
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(PLACEHOLDER_BUTTONS_AMOUNT) {
            keyEmulateUiApi.ComposableEmulatePlaceholderButton(
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}

@Composable
private fun ComposableEmulateError(emulateState: InfraredEmulateState?) {
    ComposableIconText(
        iconId = DesignSystem.drawable.ic_warning,
        textId = when (emulateState) {
            InfraredEmulateState.UPDATE_FLIPPER -> R.string.infrared_update_flipper
            InfraredEmulateState.NOT_CONNECTED -> R.string.infrared_connect_flipper
            else -> return
        }
    )
}
