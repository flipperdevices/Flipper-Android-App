package com.flipperdevices.remotecontrols.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.remotecontrols.api.BrandsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.CategoriesScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.InfraredsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.PauseSyncDialogDecomposeComponent
import com.flipperdevices.remotecontrols.api.RemoteControlsScreenDecomposeComponent
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.device.select.impl.R
import com.flipperdevices.remotecontrols.impl.api.model.RemoteControlsNavigationConfig
import com.flipperdevices.remotecontrols.impl.viewmodel.PauseSyncViewModel
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.popOr
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Provider
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, PauseSyncDialogDecomposeComponent.Factory::class)
class PauseSyncDialogDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onProceed: () -> Unit,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val pauseSyncViewModelProvider: Provider<PauseSyncViewModel>
) : PauseSyncDialogDecomposeComponent(componentContext) {
    @Composable
    override fun Render() {
        val pauseSyncViewModel = viewModelWithFactory(null) {
            pauseSyncViewModelProvider.get()
        }
        val isDialogVisible by pauseSyncViewModel.isDialogVisible.collectAsState()

        val dialogModel = remember(onBack, onProceed, pauseSyncViewModel) {
            FlipperMultiChoiceDialogModel.Builder()
                .setTitle(composableText = { stringResource(R.string.rcm_pause_sync_dialog_title) })
                .setDescription(composableText = { stringResource(R.string.rcm_pause_sync_dialog_desc) })
                .setOnDismissRequest(onBack::invoke)
                .addButton(
                    textComposable = { stringResource(R.string.rcm_pause_sync_dialog_proceed) },
                    onClick = {
                        pauseSyncViewModel.stop()
                        onProceed.invoke()
                    },
                    isActive = true
                )
                .addButton(
                    textComposable = { stringResource(R.string.rcm_pause_sync_dialog_cancel) },
                    onClick = {
                        pauseSyncViewModel.dismiss()
                        onBack.invoke()
                    }
                )
                .build()
        }
        if (isDialogVisible) {
            FlipperMultiChoiceDialog(model = dialogModel)
        }
    }

}
