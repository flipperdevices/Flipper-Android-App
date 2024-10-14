package com.flipperdevices.archive.category.composable.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flipperdevices.archive.category.R
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setDescription
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle

@Composable
internal fun ComposableDeleteAllDialog(
    onAction: () -> Unit,
    onCancel: () -> Unit
) {
    val dialogModel = remember(onCancel, onAction) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.dialog_delete_all_title)
            .setDescription(R.string.dialog_delete_all_desc)
            .setOnDismissRequest(onCancel)
            .addButton(R.string.dialog_delete_all_confirm, onAction, isActive = true)
            .addButton(R.string.dialog_cancel, onCancel)
            .build()
    }
    FlipperMultiChoiceDialog(model = dialogModel)
}
