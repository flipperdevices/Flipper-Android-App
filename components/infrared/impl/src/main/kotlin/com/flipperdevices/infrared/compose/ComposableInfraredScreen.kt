package com.flipperdevices.infrared.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.infrared.compose.screens.ComposableInfraredErrorScreen
import com.flipperdevices.infrared.compose.screens.ComposableInfraredLoadingScreen
import com.flipperdevices.infrared.compose.screens.ComposableInfraredSuccessScreen
import com.flipperdevices.infrared.viewmodel.InfraredViewModel
import com.flipperdevices.keyscreen.api.KeyEmulateApi
import com.flipperdevices.keyscreen.api.state.KeyScreenState

@Composable
internal fun ComposableInfraredScreen(
    viewModel: InfraredViewModel,
    onShare: (FlipperKeyPath) -> Unit,
    onRename: (FlipperKeyPath) -> Unit,
    onBack: () -> Unit,
    keyEmulateApi: KeyEmulateApi,
) {
    val state by viewModel.state().collectAsState()

    when (val localState = state) {
        is KeyScreenState.Error -> ComposableInfraredErrorScreen(errorId = localState.reason)
        KeyScreenState.InProgress -> ComposableInfraredLoadingScreen()
        is KeyScreenState.Ready -> {
            ComposableInfraredSuccessScreen(
                keyEmulateApi = keyEmulateApi,
                state = localState,
                viewModel = viewModel,
                onShare = { onShare(localState.flipperKey.getKeyPath()) },
                onBack = onBack,
                onRename = onRename
            )
        }
    }
}
