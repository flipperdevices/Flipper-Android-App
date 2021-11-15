package com.flipperdevices.share.receive.composable

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flipperdevices.deeplink.model.DeeplinkContent
import com.flipperdevices.share.common.composable.ComposableAlertDialog
import com.flipperdevices.share.receive.R
import com.flipperdevices.share.receive.viewmodel.ReceiveViewModel
import com.flipperdevices.share.receive.viewmodel.ReceiveViewModelFactory

@Composable
fun ComposableReceive(
    deeplinkContent: DeeplinkContent,
    flipperPath: String,
    onSuccessful: () -> Unit,
    onCancel: () -> Unit,
    viewModel: ReceiveViewModel = viewModel(
        factory = ReceiveViewModelFactory(
            deeplinkContent,
            flipperPath,
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val receiveState by viewModel.getReceiveState().collectAsState()
    if (receiveState.processCompleted) {
        onSuccessful()
        return
    }
    if (!receiveState.dialogShown) {
        onCancel()
        return
    }
    ComposableAlertDialog(
        title = stringResource(
            R.string.receive_dialog_title,
            deeplinkContent.filename() ?: "Unknown"
        ),
        downloadProgress = receiveState.downloadProgress,
        onCancel = {
            viewModel.cancelUpload()
        }
    )
}
