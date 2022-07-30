package com.flipperdevices.filemanager.receive.composable

import androidx.compose.runtime.Composable
import com.flipperdevices.deeplink.model.DeeplinkContent

@Composable
fun ComposableReceive(
    deeplinkContent: DeeplinkContent,
    flipperPath: String,
    onSuccessful: () -> Unit,
    onCancel: () -> Unit

) {
}
