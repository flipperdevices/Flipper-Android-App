package com.flipperdevices.remotecontrols.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.remotecontrols.api.PauseSyncDialogDecomposeComponent
import com.flipperdevices.remotecontrols.main.impl.R
import com.flipperdevices.remotecontrols.impl.viewmodel.PauseSyncViewModel
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedInject
import uk.kulikov.metro.assisted.ContributesAssistedFactory
import dev.zacsweers.metro.Provider

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
            pauseSyncViewModelProvider.invoke()
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
