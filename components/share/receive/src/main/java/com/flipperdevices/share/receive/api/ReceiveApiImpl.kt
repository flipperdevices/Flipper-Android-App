package com.flipperdevices.share.receive.api

import android.net.Uri
import androidx.compose.runtime.Composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.share.api.ReceiveApi
import com.flipperdevices.share.receive.composable.ComposableReceive
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class)
class ReceiveApiImpl @Inject constructor() : ReceiveApi {
    @Composable
    override fun AlertDialogUpload(receiveFileUri: Uri, flipperPath: String, onCancel: () -> Unit) {
        ComposableReceive(
            receiveFileUri = receiveFileUri,
            flipperPath = flipperPath,
            onCancel = onCancel
        )
    }
}
