package com.flipperdevices.filemanager.editor.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.filemanager.editor.viewmodel.FileNameViewModel
import com.flipperdevices.filemanager.ui.components.name.NameDialog

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
                title = "Save as file",
                buttonText = "Save",
                subtitle = "Allowed characters: $AVAILABLE_CHARACTERS",
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
