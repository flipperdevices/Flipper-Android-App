package com.flipperdevices.faphub.errors.api

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface FapHubComposableErrorsRenderer {
    @Suppress("FunctionNaming")
    fun LazyListScope.ComposableThrowableErrorListItem(
        throwable: Throwable,
        onRetry: () -> Unit,
        modifier: Modifier,
        fapErrorSize: FapErrorSize
    )

    @Composable
    fun ComposableThrowableError(
        throwable: Throwable,
        onRetry: () -> Unit,
        modifier: Modifier,
        fapErrorSize: FapErrorSize
    )
}
