package com.flipperdevices.filemanager.listing.impl.composable.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.filemanager.listing.impl.viewmodel.CreateFileViewModel
import com.flipperdevices.filemanager.ui.components.name.NameDialog
import okio.Path
import com.flipperdevices.filemanager.listing.impl.R as FML

private const val AVAILABLE_CHARACTERS = "“0-9”, “A-Z”, “a-z”, “!#\\\$%&'()-@^_`{}~”"

@Composable
fun CreateFileDialogComposable(
    createFileViewModel: CreateFileViewModel,
    path: Path
) {
    val createFileState by createFileViewModel.state.collectAsState()

    when (val localCreateFileState = createFileState) {
        CreateFileViewModel.State.Pending -> Unit
        is CreateFileViewModel.State.Visible -> {
            NameDialog(
                value = localCreateFileState.name,
                title = stringResource(FML.string.fml_create_file_title),
                buttonText = stringResource(
                    id = when (localCreateFileState.itemType) {
                        CreateFileViewModel.ItemType.FILE -> FML.string.fml_create_file_file_btn
                        CreateFileViewModel.ItemType.FOLDER -> FML.string.fml_create_file_folder_btn
                    }
                ),
                subtitle = stringResource(
                    FML.string.fml_create_file_allowed_chars,
                    AVAILABLE_CHARACTERS
                ),
                onFinish = { createFileViewModel.onCreate(path) },
                isError = !localCreateFileState.isValid,
                isEnabled = !localCreateFileState.isLoading,
                needShowOptions = localCreateFileState.needShowOptions,
                onTextChange = createFileViewModel::onNameChange,
                onDismissRequest = createFileViewModel::dismiss,
                onOptionSelect = createFileViewModel::onOptionSelected,
                options = localCreateFileState.options
            )
        }
    }
}
