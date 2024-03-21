package com.flipperdevices.nfc.mfkey32.screen.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.nfc.mfkey32.api.MfKey32DecomposeComponent
import com.flipperdevices.nfc.mfkey32.screen.composable.ComposableMfKey32Dialog
import com.flipperdevices.nfc.mfkey32.screen.composable.ComposableMfKey32Screen
import com.flipperdevices.nfc.mfkey32.screen.model.MfKey32State
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.FlipperColorViewModel
import com.flipperdevices.nfc.mfkey32.screen.viewmodel.MfKey32ViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, MfKey32DecomposeComponent.Factory::class)
class MfKey32DecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val mfKey32ViewModelProvider: Provider<MfKey32ViewModel>,
    private val flipperColorViewModelProvider: Provider<FlipperColorViewModel>
) : MfKey32DecomposeComponent(componentContext) {
    private val isBackPressHandledFlow = MutableStateFlow(false)
    private val backCallback = BackCallback { isBackPressHandledFlow.update { true } }

    init {
        backHandler.register(backCallback)
    }

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val isBackPressHandled by isBackPressHandledFlow.collectAsState()

        val viewModel = viewModelWithFactory(key = null) {
            mfKey32ViewModelProvider.get()
        }
        val state by viewModel.getMfKey32State().collectAsState()
        val foundedKeys by viewModel.getFoundedInformation().collectAsState()

        var isDisplayDialog by remember { mutableStateOf(false) }
        if (isDisplayDialog) {
            ComposableMfKey32Dialog(
                onContinue = { isDisplayDialog = false },
                onAbort = {
                    isDisplayDialog = false
                    onBack()
                }
            )
        }
        LaunchedEffect(key1 = isBackPressHandled) {
            if (isBackPressHandled) {
                when (state) {
                    is MfKey32State.Calculating,
                    is MfKey32State.DownloadingRawFile,
                    MfKey32State.Uploading -> {
                        isDisplayDialog = true
                        isBackPressHandledFlow.emit(false)
                    }

                    is MfKey32State.Error,
                    MfKey32State.WaitingForFlipper,
                    is MfKey32State.Saved -> withContext(Dispatchers.Main) {
                        onBack()
                    }
                }
            }
        }

        val flipperColorViewModel = viewModelWithFactory(key = null) {
            flipperColorViewModelProvider.get()
        }
        val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()

        ComposableMfKey32Screen(
            state = state,
            foundedKeys = foundedKeys,
            onBack = { isBackPressHandledFlow.update { true } },
            flipperColor = flipperColor
        )
    }
}
