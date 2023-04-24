package com.flipperdevices.infrared.editor.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.infrared.editor.R

@Composable
internal fun ComposableInfraredDialog(
    state: Boolean,
    onSave: () -> Unit,
    onNotSave: () -> Unit,
    onDismiss: () -> Unit
) {
    if (state.not()) return

    val dialogModel = remember(onSave, onNotSave, onDismiss) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.infrared_editor_dialog_title)
            .setDescription(R.string.infrared_editor_dialog_text)
            .setOnDismissRequest(onDismiss)
            .addButton(R.string.infrared_editor_dialog_btn_save, onSave, isActive = true)
            .addButton(R.string.infrared_editor_dialog_btn_not_save, onNotSave)
            .build()
    }

    FlipperMultiChoiceDialog(model = dialogModel)
}

