package com.flipperdevices.share.receive.composable

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.share.common.composable.ComposableAlertDialog
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.util.filename
import com.flipperdevices.share.receive.viewmodel.ReceiveViewModel
import com.flipperdevices.share.receive.viewmodel.ReceiveViewModelFactory

@Composable
fun ComposableReceive(
    receiveFileUri: Uri,
    flipperPath: String,
    onCancel: () -> Unit,
    viewModel: ReceiveViewModel = viewModel(
        key = receiveFileUri.path,
        factory = ReceiveViewModelFactory(
            receiveFileUri,
            flipperPath,
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val receiveState by viewModel.getReceiveState().collectAsState()
    if (!receiveState.dialogShown) {
        onCancel()
    }
    ComposableAlertDialog(
        title = stringResource(
            R.string.receive_dialog_title,
            receiveFileUri.filename() ?: ""
        ),
        downloadProgress = receiveState.downloadProgress,
        onCancel = {
            viewModel.cancelUpload()
        }
    )
}
