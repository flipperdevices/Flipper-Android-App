package com.flipperdevices.infrared.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.SetUpNavigationBarColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.impl.R
import com.flipperdevices.infrared.impl.composable.components.ComposableInfraredAppBar
import com.flipperdevices.infrared.impl.viewmodel.InfraredEmulateState
import com.flipperdevices.infrared.impl.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.flipperdevices.core.ui.res.R as DesignSystem

@Composable
internal fun ComposableInfraredScreen(
    navController: NavController,
    viewModel: InfraredViewModel,
    keyScreenApi: KeyScreenApi,
    keyEmulateApi: KeyEmulateApi,
    keyEmulateUiApi: KeyEmulateUiApi,
    onEdit: (FlipperKeyPath) -> Unit,
    onRename: (FlipperKeyPath) -> Unit,
    onShare: () -> Unit,
) {
    val state by viewModel.getState().collectAsState()
    val emulateState by viewModel.getEmulateState().collectAsState()
    when (val localState = state) {
        is KeyScreenState.Error -> ComposableKeyScreenError(stringResource(id = localState.reason))
        KeyScreenState.InProgress -> ComposableKeyScreenLoading()
        is KeyScreenState.Ready ->
            Column(modifier = Modifier.fillMaxSize()) {
                ComposableInfraredAppBar(
                    onBack = navController::popBackStack,
                    onEdit = { onEdit(localState.flipperKey.getKeyPath()) },
                    onDelete = { viewModel.onDelete(onEndAction = navController::popBackStack) },
                    onShare = onShare,
                    keyName = localState.flipperKey.path.nameWithoutExtension,
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
                                .padding(vertical = 14.dp)
                        )
                    },
                    keyEmulateContent = { config ->
                        keyEmulateApi.ComposableEmulateButton(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            emulateConfig = config,
                            isSynchronized = localState.flipperKey.synchronized
                        )
                    },
                    keyEmulateErrorContent = {
                        ComposableEmulateError(keyEmulateUiApi, emulateState)
                    }
                )
            }
    }
    SetUpNavigationBarColor(color = LocalPallet.current.background)
}

@Composable
private fun ComposableEmulateError(
    keyEmulateUiApi: KeyEmulateUiApi,
    emulateState: InfraredEmulateState?,
) {
    val placeholderColor = LocalPallet.current.text8.copy(alpha = 0.2f)

    keyEmulateUiApi.ComposableEmulateButtonWithText(
        modifier = Modifier,
        buttonModifier = Modifier
            .padding(horizontal = 20.dp)
            .placeholder(
                visible = true,
                color = placeholderColor,
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = LocalPallet.current.placeholder
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        buttonTextId = R.string.infrared_loading_emulate,
        color = LocalPallet.current.text8,
        textId = when (emulateState) {
            InfraredEmulateState.UPDATE_FLIPPER -> R.string.infrared_update_flipper
            InfraredEmulateState.NOT_CONNECTED -> R.string.infrared_connect_flipper
            else -> null
        },
        iconId = DesignSystem.drawable.ic_warning,
        picture = null,
        progress = null,
        progressColor = Color.Transparent
    )
}
