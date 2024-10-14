package com.flipperdevices.nfceditor.impl.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setDescription
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.nfceditor.impl.R

@Composable
fun ComposableNfcEditExitDialog(
    onSave: () -> Unit,
    onSaveAs: () -> Unit,
    onNotSave: () -> Unit,
    onDismiss: () -> Unit
) {
    val dialogModel = remember(onSave, onSaveAs, onNotSave, onDismiss) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.nfc_dialog_title)
            .setDescription(R.string.nfc_dialog_text)
            .setOnDismissRequest(onDismiss)
            .addButton(R.string.nfc_dialog_btn_save, onSave, isActive = true)
            .addButton(R.string.nfc_dialog_btn_not_save, onNotSave)
            .addButton(R.string.nfc_dialog_btn_save_as, onSaveAs)
            .build()
    }

    FlipperMultiChoiceDialog(model = dialogModel)
}
