package com.flipperdevices.filemanager.ui.components.error

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import flipperapp.components.filemngr.ui_components.generated.resources.Res
import flipperapp.components.filemngr.ui_components.generated.resources.filemngr_error_unknown_desc
import flipperapp.components.filemngr.ui_components.generated.resources.filemngr_error_unknown_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun UnknownErrorComposable(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
) {
    ErrorContentComposable(
        modifier = modifier,
        onRetry = onRetry,
        text = stringResource(Res.string.filemngr_error_unknown_title),
        desc = stringResource(Res.string.filemngr_error_unknown_desc)
    )
}
