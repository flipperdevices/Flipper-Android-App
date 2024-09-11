package com.flipperdevices.keyscreen.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.preference.pb.Settings
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.model.KeyScreenNavigationConfig
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import com.flipperdevices.ui.decompose.statusbar.ThemeStatusBarIconStyleProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Suppress("LongParameterList")
class KeyScreenViewDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val keyPath: FlipperKeyPath,
    @Assisted private val navigation: StackNavigation<KeyScreenNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val keyScreenViewModelFactory: KeyScreenViewModel.Factory,
    private val shareBottomApi: ShareBottomUIApi,
    private val synchronizationUiApi: SynchronizationUiApi,
    private val keyEmulateApi: KeyEmulateApi,
    dataStore: DataStore<Settings>
) : ScreenDecomposeComponent(componentContext) {
    private val isBackPressHandledFlow = MutableStateFlow(false)
    private val backCallback = BackCallback(false) { isBackPressHandledFlow.update { true } }
    private val themeStatusBarIconStyleProvider = ThemeStatusBarIconStyleProvider(dataStore)

    init {
        backHandler.register(backCallback)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val viewModel = viewModelWithFactory(key = keyPath.toString()) {
            keyScreenViewModelFactory(keyPath)
        }
        shareBottomApi.ComposableShareBottomSheet(
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
            ComposableKeyScreen(
                modifier = Modifier
                    .background(LocalPallet.current.background)
                    .navigationBarsPadding(),
                viewModel = viewModel,
                synchronizationUiApi = synchronizationUiApi,
                keyEmulateApi = keyEmulateApi,
                onShare = onShare,
                onBack = onBack::invoke,
                onOpenNfcEditor = {
                    viewModel.openNfcEditor { flipperKeyPath ->
                        navigation.pushToFront(KeyScreenNavigationConfig.NfcEdit(flipperKeyPath))
                    }
                },
                onOpenEditScreen = { flipperKeyPath ->
                    navigation.pushToFront(KeyScreenNavigationConfig.KeyEdit(flipperKeyPath))
                },
                componentContext = this
            )
        }
    }

    override fun isStatusBarIconLight(systemIsDark: Boolean): Boolean {
        return themeStatusBarIconStyleProvider.isStatusBarIconLight(systemIsDark)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            navigation: StackNavigation<KeyScreenNavigationConfig>,
            onBack: DecomposeOnBackParameter
        ): KeyScreenViewDecomposeComponentImpl
    }
}
