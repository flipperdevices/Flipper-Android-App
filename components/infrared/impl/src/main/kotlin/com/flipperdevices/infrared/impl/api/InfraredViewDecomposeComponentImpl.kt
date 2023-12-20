package com.flipperdevices.infrared.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.infrared.impl.composable.ComposableInfraredScreen
import com.flipperdevices.infrared.impl.model.InfraredNavigationConfig
import com.flipperdevices.infrared.impl.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class InfraredViewDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val keyPath: FlipperKeyPath,
    @Assisted private val navigation: StackNavigation<InfraredNavigationConfig>,
    private val infraredViewModelFactory: InfraredViewModel.Factory,
    private val shareBottomUiApi: ShareBottomUIApi,
    private val keyScreenApi: KeyScreenApi,
    private val keyEmulateApi: KeyEmulateApi,
    private val keyEmulateUiApi: KeyEmulateUiApi,
) : DecomposeComponent, ComponentContext by componentContext {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val viewModel: InfraredViewModel = viewModelWithFactory(key = keyPath.toString()) {
            infraredViewModelFactory(keyPath)
        }
        shareBottomUiApi.ComposableShareBottomSheet(viewModel.keyPath) { onShare ->
            ComposableInfraredScreen(
                viewModel = viewModel,
                keyScreenApi = keyScreenApi,
                keyEmulateApi = keyEmulateApi,
                keyEmulateUiApi = keyEmulateUiApi,
                onEdit = {
                    navigation.push(InfraredNavigationConfig.Edit(it))
                },
                onRename = {
                    navigation.push(InfraredNavigationConfig.Rename(it))
                },
                onShare = onShare,
                onBack = navigation::pop
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            navigation: StackNavigation<InfraredNavigationConfig>
        ): InfraredViewDecomposeComponentImpl
    }
}
