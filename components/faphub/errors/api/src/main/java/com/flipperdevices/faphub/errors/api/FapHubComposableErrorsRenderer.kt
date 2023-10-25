package com.flipperdevices.faphub.errors.api

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.errors.api.throwable.StableThrowable

@Immutable
interface FapHubComposableErrorsRenderer {
    @Suppress("FunctionNaming")
    fun LazyListScope.ComposableThrowableErrorListItem(
        throwable: StableThrowable,
        onRetry: () -> Unit,
        modifier: Modifier,
        fapErrorSize: FapErrorSize
    )

    @Composable
    fun ComposableThrowableError(
        throwable: StableThrowable,
        onRetry: () -> Unit,
        modifier: Modifier,
        fapErrorSize: FapErrorSize
    )
}
