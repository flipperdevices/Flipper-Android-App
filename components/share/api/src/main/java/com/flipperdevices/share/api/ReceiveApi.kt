package com.flipperdevices.share.api

import android.net.Uri
import androidx.compose.runtime.Composable

interface ReceiveApi {
    @Composable
    fun AlertDialogUpload(receiveFileUri: Uri, flipperPath: String, onCancel: () -> Unit)
}
