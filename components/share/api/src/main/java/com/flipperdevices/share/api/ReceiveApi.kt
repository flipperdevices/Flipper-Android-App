package com.flipperdevices.share.api

import androidx.compose.runtime.Composable
import com.flipperdevices.deeplink.model.DeeplinkContent

interface ReceiveApi {
    @Composable
    fun AlertDialogUpload(
        deeplinkContent: DeeplinkContent,
        flipperPath: String,
        onSuccessful: () -> Unit,
        onCancel: () -> Unit
    )
}
