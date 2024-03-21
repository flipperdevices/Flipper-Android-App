package com.flipperdevices.updater.screen.api

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.updater.api.UpdaterDecomposeComponent
import com.flipperdevices.updater.model.UpdateRequest
import com.flipperdevices.updater.screen.composable.ComposableCancelDialog
import com.flipperdevices.updater.screen.composable.ComposableUpdaterScreen
import com.flipperdevices.updater.screen.model.UpdaterScreenState
import com.flipperdevices.updater.screen.viewmodel.FlipperColorViewModel
import com.flipperdevices.updater.screen.viewmodel.UpdaterViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, UpdaterDecomposeComponent.Factory::class)
class UpdaterDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val updateRequest: UpdateRequest?,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val flipperColorViewModelProvider: Provider<FlipperColorViewModel>,
    private val updaterViewModelProvider: Provider<UpdaterViewModel>
) : UpdaterDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val flipperColorViewModel = viewModelWithFactory(key = null) {
            flipperColorViewModelProvider.get()
        }
        val updaterViewModel = viewModelWithFactory(key = null) {
            updaterViewModelProvider.get()
        }

        LaunchedEffect(key1 = Unit) {
            updaterViewModel.start(updateRequest)
        }

        val context = LocalContext.current
        DisposableEffect(Unit) {
            val window = (context as? Activity)?.window
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            onDispose {
                window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        val flipperColor by flipperColorViewModel.getFlipperColor().collectAsState()

        val updaterScreenState by updaterViewModel.getState().collectAsState()
        if (updaterScreenState is UpdaterScreenState.Finish) {
            LaunchedEffect(key1 = Unit) {
                onBack()
            }
        }

        val onAbortUpdate = updaterViewModel::cancelUpdate
        var isCancelDialogOpen by remember { mutableStateOf(false) }
        ComposableUpdaterScreen(
            updaterScreenState = updaterScreenState,
            flipperColor = flipperColor,
            onCancel = { isCancelDialogOpen = true },
            onRetry = { updaterViewModel.retry(updateRequest) }
        )
        if (isCancelDialogOpen) {
            when (updaterScreenState) {
                is UpdaterScreenState.Failed -> onAbortUpdate()
                else ->
                    ComposableCancelDialog(
                        onAbort = {
                            isCancelDialogOpen = false
                            updaterViewModel.cancelUpdate()
                        },
                        onContinue = { isCancelDialogOpen = false }
                    )
            }
        }
    }
}
