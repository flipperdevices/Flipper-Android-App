package com.flipperdevices.filemanager.impl.composable

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.share.api.ReceiveApi

@Composable
fun ComposableFileManagerSave(
    files: List<FileItem>,
    onOpenFolder: (FileItem) -> Unit,
    onClickSaveButton: () -> Unit
) {
    Scaffold(
        topBar = { ComposableSaveTopBar(onClickSaveButton) }
    ) { scaffoldPaddings ->
        ComposableFileManager(
            files,
            Modifier.padding(scaffoldPaddings),
            onOpenFolder
        )
    }
}

@Composable
fun ComposableFileManagerSaveWithDialog(
    receiveApi: ReceiveApi,
    receiveUri: Uri,
    currentPath: String,
    files: List<FileItem>,
    onOpenFolder: (FileItem) -> Unit
) {
    var openSaveDialog by remember { mutableStateOf(false) }
    ComposableFileManagerSave(files, onOpenFolder) {
        openSaveDialog = true
    }
    if (openSaveDialog) {
        receiveApi.AlertDialogUpload(
            receiveFileUri = receiveUri,
            flipperPath = currentPath
        ) {
            openSaveDialog = false
        }
    }
}

@Composable
private fun ComposableSaveTopBar(onClickSaveButton: () -> Unit) {
    TopAppBar(title = {
        Text(stringResource(R.string.filemanager_save_title))
    }, actions = {
        IconButton(onClick = onClickSaveButton) {
            Icon(
                painter = painterResource(R.drawable.ic_ok),
                contentDescription = stringResource(R.string.filemanager_save_action)
            )
        }
    })
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ComposableFileManagerSavePreview() {
    ComposableFileManagerSave(
        listOf(FileItem.DUMMY), onOpenFolder = {},
        onClickSaveButton = {}
    )
}
