package com.flipperdevices.selfupdater.impl.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.selfupdater.impl.viewmodel.SelfUpdaterViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
internal fun ComposableSelfUpdate(
    viewModel: SelfUpdaterViewModel = tangleViewModel()
) {
    val isDialogShow by viewModel.dialogState().collectAsState()
    if (isDialogShow.not()) return

    ComposableSelfUpdateDialog(
        onCancel = viewModel::cancelDialog,
        onUpdate = {}
    )
}

@Composable
private fun ComposableSelfUpdateDialog(
    onCancel: () -> Unit,
    onUpdate: () -> Unit
) {
    val dialogModel = remember(onCancel, onUpdate) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(android.R.string.cut)
            .setDescription(android.R.string.cut)
            .setOnDismissRequest(onCancel)
            .addButton(android.R.string.cut, onUpdate, isActive = true)
            .addButton(android.R.string.cut, onCancel)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}
