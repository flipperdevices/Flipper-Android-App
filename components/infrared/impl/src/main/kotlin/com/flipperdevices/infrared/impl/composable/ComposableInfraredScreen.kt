package com.flipperdevices.infrared.impl.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.SetUpNavigationBarColor
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.impl.composable.components.bar.ComposableInfraredAppBar
import com.flipperdevices.infrared.impl.model.InfraredTab
import com.flipperdevices.infrared.impl.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.keyscreen.model.FavoriteState
import com.flipperdevices.keyscreen.model.KeyScreenState
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenError
import com.flipperdevices.keyscreen.shared.screen.ComposableKeyScreenLoading

@Composable
internal fun ComposableInfraredScreen(
    navController: NavController,
    viewModel: InfraredViewModel,
    keyScreenApi: KeyScreenApi,
    keyEmulateApi: KeyEmulateApi,
    onEdit: (FlipperKeyPath) -> Unit,
    onRename: (FlipperKeyPath) -> Unit,
    onShare: () -> Unit,
) {
    val state by viewModel.getState().collectAsState()
    var currentTab by remember { mutableStateOf(InfraredTab.REMOTE) }

    when (val localState = state) {
        is KeyScreenState.Error -> {
            ComposableKeyScreenError(text = stringResource(id = localState.reason))
        }
        KeyScreenState.InProgress -> {
            ComposableKeyScreenLoading()
        }
        is KeyScreenState.Ready -> {
            Column(modifier = Modifier.fillMaxSize()) {
                ComposableInfraredAppBar(
                    onBack = navController::popBackStack,
                    currentTab = currentTab,
                    onChangeTab = { currentTab = it },
                    onFavorite = {
                        viewModel.setFavorite(localState.favoriteState != FavoriteState.FAVORITE)
                    },
                    onEdit = { onEdit(localState.flipperKey.getKeyPath()) },
                    onRename = { viewModel.onRename(onEndAction = onRename) },
                    onDelete = { viewModel.onDelete(onEndAction = navController::popBackStack) },
                    onShare = onShare,
                    isFavorite = localState.favoriteState == FavoriteState.FAVORITE,
                )
                ComposableInfraredScreenReady(
                    currentTab,
                    state = localState,
                    keyCardContent = {
                        keyScreenApi.KeyCard(
                            onEdit = {},
                            onFavorite = viewModel::setFavorite,
                            state = localState,
                            modifier = Modifier
                                .padding(horizontal = 24.dp)
                                .padding(top = 14.dp)
                        )
                    },
                    keyEmulateContent = { config ->
                        keyEmulateApi.ComposableEmulateButton(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            emulateConfig = config,
                            isSynchronized = localState.flipperKey.synchronized
                        )
                    }
                )
            }
        }
    }

    SetUpNavigationBarColor(color = LocalPallet.current.background)
}
