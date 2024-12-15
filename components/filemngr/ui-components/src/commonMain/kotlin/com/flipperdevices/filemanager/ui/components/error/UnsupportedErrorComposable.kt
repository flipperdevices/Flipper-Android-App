package com.flipperdevices.filemanager.ui.components.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import flipperapp.components.filemngr.ui_components.generated.resources.filemngr_error_unsupported_desc
import flipperapp.components.filemngr.ui_components.generated.resources.filemngr_error_unsupported_title
import org.jetbrains.compose.resources.stringResource
import flipperapp.components.filemngr.ui_components.generated.resources.Res as FR

@Composable
fun UnsupportedErrorComposable(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    ErrorContentComposable(
        modifier = modifier,
        onRetry = onRetry,
        text = stringResource(FR.string.filemngr_error_unsupported_title),
        desc = stringResource(FR.string.filemngr_error_unsupported_desc)
    )
}
