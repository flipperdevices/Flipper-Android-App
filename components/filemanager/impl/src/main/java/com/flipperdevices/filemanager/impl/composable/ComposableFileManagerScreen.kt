package com.flipperdevices.filemanager.impl.composable

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.flipperdevices.deeplink.api.DeepLinkParser
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.composable.bar.ComposableFileManagerTopBar
import com.flipperdevices.filemanager.impl.composable.dialog.ComposableInputDialog
import com.flipperdevices.filemanager.impl.composable.dialog.ComposableSelectDialog
import com.flipperdevices.filemanager.impl.composable.list.ComposableFileManagerContent
import com.flipperdevices.filemanager.impl.model.CreateFileManagerAction
import com.flipperdevices.filemanager.impl.model.FileItem
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import kotlinx.coroutines.runBlocking

@Composable
fun ComposableFileManagerScreen(
    fileManagerViewModel: FileManagerViewModel,
    deepLinkParser: DeepLinkParser,
    onOpenFolder: (FileItem) -> Unit,
    onDownloadAndShareFile: (FileItem) -> Unit,
    onOpenEditor: (FileItem) -> Unit,
    onUploadFile: (path: String, DeeplinkContent) -> Unit
) {
    val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()

    var pendingDialogItem by remember { mutableStateOf<FileItem?>(null) }
    val localFileItem = pendingDialogItem

    if (localFileItem != null) {
        val chooseOptions = if (isAbleToDelete(fileManagerState.currentPath)) {
            intArrayOf(
                R.string.filemanager_open_dialog_edit,
                R.string.filemanager_open_dialog_download,
                R.string.filemanager_open_dialog_delete
            )
        } else {
            intArrayOf(
                R.string.filemanager_open_dialog_edit,
                R.string.filemanager_open_dialog_download
            )
        }
        ComposableSelectDialog(chooseOptions, onSelect = {
            when (it) {
                R.string.filemanager_open_dialog_edit -> onOpenEditor(localFileItem)
                R.string.filemanager_open_dialog_download -> onDownloadAndShareFile(localFileItem)
                R.string.filemanager_open_dialog_delete -> {
                    fileManagerViewModel.onDeleteAction(localFileItem)
                    pendingDialogItem = null
                }
                else -> pendingDialogItem = null
            }
        })
    }

    var showAddDialog by remember { mutableStateOf(false) }
    if (showAddDialog) {
        ComposableCreateActionDialog(
            onCreateAction = fileManagerViewModel::onCreateAction,
            onDismiss = { showAddDialog = false }
        )
    }

    ComposableFileManagerScreenInternal(
        fileManagerState = fileManagerState,
        deepLinkParser = deepLinkParser,
        onOpenFolder = {
            if (it.isDirectory) {
                onOpenFolder(it)
            } else {
                pendingDialogItem = it
            }
        },
        onUploadFile = {
            onUploadFile(fileManagerState.currentPath, it)
        },
        onAddButton = {
            showAddDialog = true
        }
    )
}

@Composable
private fun ComposableCreateActionDialog(
    onCreateAction: (CreateFileManagerAction, String) -> Unit,
    onDismiss: () -> Unit
) {
    var createFileManagerAction by remember { mutableStateOf<CreateFileManagerAction?>(null) }
    val localCreateFileManagerAction = createFileManagerAction

    if (localCreateFileManagerAction != null) {
        ComposableInputDialog(
            when (localCreateFileManagerAction) {
                CreateFileManagerAction.FILE -> R.string.add_dialog_title_file
                CreateFileManagerAction.FOLDER -> R.string.add_dialog_title_folder
            }
        ) {
            if (it != null) {
                onCreateAction(localCreateFileManagerAction, it)
            }
            onDismiss()
            createFileManagerAction = null
        }
    }

    ComposableSelectDialog(
        intArrayOf(
            R.string.filemanager_add_dialog_file,
            R.string.filemanager_add_dialog_folder
        ),
        onSelect = {
            when (it) {
                R.string.filemanager_add_dialog_file ->
                    createFileManagerAction = CreateFileManagerAction.FILE
                R.string.filemanager_add_dialog_folder ->
                    createFileManagerAction = CreateFileManagerAction.FOLDER
                else -> onDismiss()
            }
        }
    )
}

@Composable
private fun ComposableFileManagerScreenInternal(
    fileManagerState: FileManagerState,
    onOpenFolder: (FileItem) -> Unit,
    deepLinkParser: DeepLinkParser,
    onUploadFile: (DeeplinkContent) -> Unit,
    onAddButton: () -> Unit
) {
    val context = LocalContext.current

    val pickFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            runBlocking {
                val deeplink = deepLinkParser.fromUri(context, uri)

                if (deeplink != null && deeplink is Deeplink.RootLevel.SaveKey.ExternalContent) {
                    val deeplinkContent = deeplink.content
                    if (deeplinkContent != null) {
                        onUploadFile(deeplinkContent)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ComposableFileManagerTopBar(
                fileManagerState.currentPath,
                onClickUploadButton = {
                    pickFileLauncher.launch("*/*")
                },
                onClickAddButton = onAddButton
            )
        }
    ) { scaffoldPaddings ->
        ComposableFileManagerContent(
            modifier = Modifier.padding(scaffoldPaddings),
            fileManagerState = fileManagerState,
            onFileClick = onOpenFolder
        )
    }
}

private fun isAbleToDelete(path: String) = path.startsWith("/ext")
