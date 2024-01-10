package com.flipperdevices.keyscreen.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.core.ui.theme.LocalPallet
import com.flipperdevices.keyemulate.api.KeyEmulateApi
import com.flipperdevices.keyscreen.impl.composable.ComposableKeyScreen
import com.flipperdevices.keyscreen.impl.model.KeyScreenNavigationConfig
import com.flipperdevices.keyscreen.impl.viewmodel.KeyScreenViewModel
import com.flipperdevices.nfceditor.api.NfcEditorApi
import com.flipperdevices.share.api.ShareBottomUIApi
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
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
    private val nfcEditor: NfcEditorApi,
    private val keyEmulateApi: KeyEmulateApi,
) : DecomposeComponent, ComponentContext by componentContext {
    private val isBackPressHandledFlow = MutableStateFlow(false)
    private val backCallback = BackCallback(false) { isBackPressHandledFlow.update { true } }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val viewModel: KeyScreenViewModel = viewModelWithFactory(key = keyPath.toString()) {
            keyScreenViewModelFactory(keyPath)
        }
        shareBottomApi.ComposableShareBottomSheet(
            flipperKeyPath = viewModel.keyPath,
            onSheetStateVisible = { isShown, onClose ->
                val isBackPressHandled by isBackPressHandledFlow.collectAsState()
                backCallback.isEnabled = isShown

                LaunchedEffect(isBackPressHandled) {
                    if (isBackPressHandled) {
                        onClose()
                        isBackPressHandledFlow.emit(false)
                    }
                }
            }
        ) { onShare ->
            ComposableKeyScreen(
                viewModel = viewModel,
                synchronizationUiApi = synchronizationUiApi,
                nfcEditorApi = nfcEditor,
                keyEmulateApi = keyEmulateApi,
                onShare = onShare,
                onBack = onBack::invoke,
                onOpenNfcEditor = {
                    viewModel.openNfcEditor { flipperKeyPath ->
                        navigation.push(KeyScreenNavigationConfig.NfcEdit(flipperKeyPath))
                    }
                },
                onOpenEditScreen = { flipperKeyPath ->
                    navigation.push(KeyScreenNavigationConfig.KeyEdit(flipperKeyPath))
                },
                modifier = Modifier
                    .background(LocalPallet.current.background)
                    .navigationBarsPadding(),
            )
        }
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
