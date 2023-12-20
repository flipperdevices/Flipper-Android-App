package com.flipperdevices.nfceditor.impl.api

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.nfceditor.impl.R
import com.flipperdevices.nfceditor.impl.composable.ComposableNfcEditorScreen
import com.flipperdevices.nfceditor.impl.model.NfcEditorNavigationConfig
import com.flipperdevices.nfceditor.impl.viewmodel.NfcEditorViewModel
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class NfcEditorScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val flipperKeyPath: FlipperKeyPath,
    @Assisted private val navigation: StackNavigation<NfcEditorNavigationConfig>,
    private val nfcEditorViewModelFactory: NfcEditorViewModel.Factory
) : DecomposeComponent, ComponentContext by componentContext {
    private val isBackPressHandledFlow = MutableStateFlow(false)
    private val backCallback = BackCallback { isBackPressHandledFlow.update { true } }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val isBackPressHandled by isBackPressHandledFlow.collectAsState()
            val saveAsTitle = LocalContext.current.getString(R.string.nfc_dialog_save_as_title)
            val nfcEditorViewModel = viewModelWithFactory(flipperKeyPath.toString()) {
                nfcEditorViewModelFactory(flipperKeyPath)
            }
            LaunchedEffect(isBackPressHandled) {
                if (isBackPressHandled) {
                    withContext(Dispatchers.Main) {
                        nfcEditorViewModel.onProcessBack(navigation::pop)
                    }
                }
            }
            ComposableNfcEditorScreen(
                onBack = navigation::pop,
                onSaveEndAction = navigation::pop,
                onSaveAsEndAction = { notSavedFlipperKey ->
                    navigation.push(NfcEditorNavigationConfig.Save(notSavedFlipperKey, saveAsTitle))
                },
                nfcEditorViewModel = nfcEditorViewModel
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            flipperKeyPath: FlipperKeyPath,
            navigation: StackNavigation<NfcEditorNavigationConfig>
        ): NfcEditorScreenDecomposeComponentImpl
    }
}
