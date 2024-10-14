package com.flipperdevices.infrared.editor.compose

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialog
import com.flipperdevices.core.ui.dialog.composable.multichoice.FlipperMultiChoiceDialogModel
import com.flipperdevices.core.ui.dialog.composable.multichoice.addButton
import com.flipperdevices.core.ui.dialog.composable.multichoice.setDescription
import com.flipperdevices.core.ui.dialog.composable.multichoice.setTitle
import com.flipperdevices.core.ui.theme.FlipperThemeInternal
import com.flipperdevices.infrared.editor.R

@Composable
internal fun ComposableInfraredEditorDialog(
    isShow: Boolean,
    onSave: () -> Unit,
    onDoNotSave: () -> Unit,
    onDismissDialog: () -> Unit
) {
    if (isShow.not()) {
        return
    }

    val dialogModel = remember(onSave, onDoNotSave) {
        FlipperMultiChoiceDialogModel.Builder()
            .setTitle(R.string.infrared_editor_dialog_title)
            .setDescription(R.string.infrared_editor_dialog_desc)
            .setOnDismissRequest(onDismissDialog)
            .addButton(R.string.infrared_editor_dialog_save, onSave, isActive = true)
            .addButton(R.string.infrared_editor_dialog_do_not_save, onDoNotSave)
            .build()
    }

    FlipperMultiChoiceDialog(model = dialogModel)
}

@Preview
@Composable
private fun PreviewComposableInfraredEditorDialogLight() {
    FlipperThemeInternal {
        ComposableInfraredEditorDialog(
            isShow = true,
            onSave = {},
            onDoNotSave = {},
            onDismissDialog = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewComposableInfraredEditorDialogDark() {
    FlipperThemeInternal {
        ComposableInfraredEditorDialog(
            isShow = true,
            onSave = {},
            onDoNotSave = {},
            onDismissDialog = {}
        )
    }
}
