package com.flipperdevices.infrared.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.infrared.impl.composable.ComposableInfraredScreen
import com.flipperdevices.infrared.impl.model.InfraredNavigationConfig
import com.flipperdevices.infrared.impl.viewmodel.InfraredViewModel
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.keyscreen.api.KeyScreenApi
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Suppress("LongParameterList")
class InfraredViewDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val keyPath: FlipperKeyPath,
    @Assisted private val navigation: StackNavigation<InfraredNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val infraredViewModelFactory: InfraredViewModel.Factory,
    private val shareBottomUiApi: ShareBottomUIApi,
    private val keyScreenApi: KeyScreenApi,
    private val keyEmulateApi: KeyEmulateApi,
    private val keyEmulateUiApi: KeyEmulateUiApi,
) : ScreenDecomposeComponent(componentContext) {
    private val isBackPressHandledFlow = MutableStateFlow(false)
    private val backCallback = BackCallback(false) { isBackPressHandledFlow.update { true } }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val viewModel: InfraredViewModel = viewModelWithFactory(key = keyPath.toString()) {
            infraredViewModelFactory(keyPath)
        }

        shareBottomUiApi.ComposableShareBottomSheet(
            provideFlipperKeyPath = viewModel::getKeyPath,
            onSheetStateVisible = { isShown, onClose ->
                val isBackPressHandled by isBackPressHandledFlow.collectAsState()
                backCallback.isEnabled = isShown

                LaunchedEffect(isBackPressHandled) {
                    if (isBackPressHandled) {
                        onClose()
                        isBackPressHandledFlow.emit(false)
                    }
                }
            },
            componentContext = this
        ) { onShare ->
            ComposableInfraredScreen(
                modifier = Modifier.background(LocalPallet.current.background)
                    .navigationBarsPadding(),
                viewModel = viewModel,
                keyScreenApi = keyScreenApi,
                keyEmulateApi = keyEmulateApi,
                keyEmulateUiApi = keyEmulateUiApi,
                onEdit = {
                    navigation.pushToFront(InfraredNavigationConfig.Edit(it))
                },
                onRename = {
                    navigation.pushToFront(InfraredNavigationConfig.Rename(it))
                },
                onShare = onShare,
                onBack = onBack::invoke,
                componentContext = this
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            navigation: StackNavigation<InfraredNavigationConfig>,
            onBack: DecomposeOnBackParameter
        ): InfraredViewDecomposeComponentImpl
    }
}
