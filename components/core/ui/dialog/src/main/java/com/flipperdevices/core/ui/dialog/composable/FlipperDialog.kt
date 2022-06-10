package com.flipperdevices.core.ui.dialog.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun FlipperDialog(
    modifier: Modifier = Modifier,
    image: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
    buttons: @Composable () -> Unit,
    onDismissRequest: (() -> Unit)? = null,
    closeOnClickOutside: Boolean = true
) {
    Dialog(onDismissRequest = {
        if (closeOnClickOutside) {
            onDismissRequest?.invoke()
        }
    }) {
        Box(modifier = modifier) {
            FlipperDialogContent(
                image,
                title,
                text,
                buttons,
                onDismissRequest
            )
        }
    }
}
