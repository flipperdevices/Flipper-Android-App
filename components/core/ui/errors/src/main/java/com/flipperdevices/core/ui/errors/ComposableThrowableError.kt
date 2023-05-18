package com.flipperdevices.core.ui.errors

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ComposableThrowableError(
    throwable: Throwable,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ComposableNoNetworkError(
        modifier = modifier,
        onRetry = onRetry
    )
}
