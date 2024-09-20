package com.flipperdevices.filemanager.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.composable.dialog.ComposableProgressDialog
import com.flipperdevices.filemanager.impl.composable.list.ComposableFileManagerContent
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.filemanager.impl.model.ShareState

@Composable
fun ComposableFileManagerDownloadScreen(
    fileManagerState: FileManagerState,
    shareState: ShareState,
    onBack: () -> Unit
) {
    if (shareState.processCompleted) {
        LaunchedEffect(onBack) {
            onBack()
        }
    }

    ComposableFileManagerDownloadScreenInternal(
        fileManagerState,
        shareState,
        onCancel = onBack
    )
}

@Composable
private fun ComposableFileManagerDownloadScreenInternal(
    fileManagerState: FileManagerState,
    shareState: ShareState,
    onCancel: () -> Unit
) {
    Box {
        ComposableFileManagerContent(fileManagerState = fileManagerState, onFileClick = {})
        ComposableProgressDialog(
            title = stringResource(
                R.string.share_dialog_title,
                shareState.name
            ),
            downloadProgress = shareState.downloadProgress,
            onCancel = onCancel
        )
    }
}
