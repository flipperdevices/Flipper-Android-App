package com.flipperdevices.faphub.errors.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface FapHubComposableErrorsRenderer {
    @Composable
    fun ComposableThrowableError(
        throwable: Throwable,
        onRetry: () -> Unit,
        modifier: Modifier
    )
}
