package com.flipperdevices.filemanager.editor.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.filemanager.editor.viewmodel.FileNameViewModel
import com.flipperdevices.filemanager.ui.components.name.NameDialog
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_dialog_button
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_dialog_chars
import flipperapp.components.filemngr.editor.impl.generated.resources.fme_save_as_dialog_title
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.editor.impl.generated.resources.Res as FME

private const val AVAILABLE_CHARACTERS = "“0-9”, “A-Z”, “a-z”, “!#\\\$%&'()-@^_`{}~”"

@Composable
fun CreateFileDialogComposable(
    fileNameViewModel: FileNameViewModel,
    onFinish: (String) -> Unit
) {
    val state by fileNameViewModel.state.collectAsState()
    when (val localState = state) {
        is FileNameViewModel.State.Editing -> {
            NameDialog(
                value = localState.name,
                title = stringResource(FME.string.fme_save_as_dialog_title),
                buttonText = stringResource(FME.string.fme_save_as_dialog_button),
                subtitle = stringResource(
                    resource = FME.string.fme_save_as_dialog_chars,
                    AVAILABLE_CHARACTERS
                ),
                onFinish = {
                    onFinish(localState.name)
                    fileNameViewModel.dismiss()
                },
                isError = !localState.isValid,
                isEnabled = true,
                needShowOptions = localState.needShowOptions,
                onTextChange = fileNameViewModel::onChange,
                onDismissRequest = fileNameViewModel::dismiss,
                onOptionSelect = fileNameViewModel::onOptionSelected,
                options = localState.options
            )
        }

        FileNameViewModel.State.Pending -> Unit
    }
}
