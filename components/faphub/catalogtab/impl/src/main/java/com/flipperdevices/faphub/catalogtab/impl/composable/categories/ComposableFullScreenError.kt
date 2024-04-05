package com.flipperdevices.faphub.catalogtab.impl.composable.categories

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.errors.api.throwable.FapHubError

@Suppress("FunctionNaming")
internal fun LazyListScope.ComposableFullScreenError(
    fapHubError: FapHubError,
    errorsRenderer: FapHubComposableErrorsRenderer,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    with(errorsRenderer) {
        ComposableThrowableErrorListItem(
            modifier = modifier,
            throwable = fapHubError,
            onRetry = onRetry,
            fapErrorSize = FapErrorSize.FULLSCREEN
        )
    }
}
