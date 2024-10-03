package com.flipperdevices.filemanager.listing.impl.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.ui.components.name.NameDialog
import okio.Path

@Composable
fun CreateFileDialogComposable(
    createFileViewModel: CreateFileViewModel,
    path: Path
) {
    val createFileState by createFileViewModel.state.collectAsState()

    when (val createFileState = createFileState) {
        CreateFileViewModel.State.Pending -> Unit
        is CreateFileViewModel.State.Visible -> {
            NameDialog(
                value = createFileState.name,
                title = "Enter Name:",
                buttonText = when (createFileState.itemType) {
                    CreateFileViewModel.ItemType.FILE -> "Create File"
                    CreateFileViewModel.ItemType.FOLDER -> "Create Folder"
                },
                subtitle = "Allowed characters",
                onFinish = { createFileViewModel.onCreate(path) },
                isError = !createFileState.isValid,
                isEnabled = !createFileState.isLoading,
                needShowOptions = createFileState.needShowOptions,
                onTextChange = createFileViewModel::onNameChange,
                onDismissRequest = createFileViewModel::dismiss,
                onOptionSelect = createFileViewModel::onOptionSelected,
                options = createFileState.options
            )
        }
    }
}
