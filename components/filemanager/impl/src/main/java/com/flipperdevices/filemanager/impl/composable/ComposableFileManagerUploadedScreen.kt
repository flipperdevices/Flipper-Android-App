package com.flipperdevices.filemanager.impl.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.flipperdevices.filemanager.impl.R
import com.flipperdevices.filemanager.impl.composable.dialog.ComposableProgressDialog
import com.flipperdevices.filemanager.impl.composable.list.ComposableFileManagerContent
import com.flipperdevices.filemanager.impl.model.FileManagerState
import com.flipperdevices.filemanager.impl.model.ShareState
import com.flipperdevices.filemanager.impl.viewmodels.FileManagerViewModel
import com.flipperdevices.filemanager.impl.viewmodels.ReceiveViewModel
import tangle.viewmodel.compose.tangleViewModel

@Composable
fun ComposableFileManagerUploadedScreen(navController: NavController) {
    val fileManagerViewModel: FileManagerViewModel = tangleViewModel()
    val fileManagerState by fileManagerViewModel.getFileManagerState().collectAsState()

    val receiveViewModel: ReceiveViewModel = tangleViewModel()
    val shareState by receiveViewModel.getReceiveState().collectAsState()

    if (shareState.processCompleted) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    ComposableFileManagerUploadedScreenInternal(
        fileManagerState,
        shareState
    ) {
        navController.popBackStack()
    }
}

@Composable
private fun ComposableFileManagerUploadedScreenInternal(
    fileManagerState: FileManagerState,
    shareState: ShareState,
    onCancel: () -> Unit
) {
    Box {
        ComposableFileManagerContent(fileManagerState = fileManagerState, onFileClick = {})
        ComposableProgressDialog(
            title = stringResource(
                R.string.receive_dialog_title,
                shareState.name
            ),
            downloadProgress = shareState.downloadProgress,
            onCancel = onCancel
        )
    }
}
