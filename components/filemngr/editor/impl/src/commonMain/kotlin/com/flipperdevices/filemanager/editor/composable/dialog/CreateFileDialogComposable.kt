package com.flipperdevices.filemanager.editor.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.filemanager.editor.viewmodel.EditFileNameViewModel
import com.flipperdevices.filemanager.ui.components.name.NameDialog
import com.flipperdevices.filemanager.util.constant.FileManagerConstants
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_dialog_button
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_dialog_chars
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_dialog_title
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME

@Composable
fun CreateFileDialogComposable(
    editFileNameViewModel: EditFileNameViewModel,
    onFinish: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val state by editFileNameViewModel.state.collectAsState()
    NameDialog(
        value = state.name,
        title = stringResource(FME.string.fme_save_as_dialog_title),
        buttonText = stringResource(FME.string.fme_save_as_dialog_button),
        subtitle = stringResource(
            resource = FME.string.fme_save_as_dialog_chars,
            FileManagerConstants.FILE_NAME_AVAILABLE_CHARACTERS
        ),
        onFinish = {
            onFinish(state.name)
        },
        isError = !state.isValid,
        isEnabled = true,
        needShowOptions = state.needShowOptions,
        onTextChange = editFileNameViewModel::onChange,
        onDismissRequest = onDismiss,
        onOptionSelect = editFileNameViewModel::onOptionSelected,
        options = state.options,
        isLoading = false
    )
}
