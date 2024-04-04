package com.flipperdevices.faphub.catalogtab.impl.composable.categories

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.ui.Modifier
import com.flipperdevices.faphub.catalogtab.impl.model.CategoriesLoadState
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.errors.api.throwable.toFapHubError

@Suppress("FunctionNaming")
internal fun LazyListScope.ComposableFullScreenError(
    categoriesLoadState: CategoriesLoadState,
    errorsRenderer: FapHubComposableErrorsRenderer,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    (categoriesLoadState as? CategoriesLoadState.Error)
        ?.throwable
        ?.toFapHubError()
        ?.let { fapHubError ->
            with(errorsRenderer) {
                ComposableThrowableErrorListItem(
                    modifier = modifier,
                    throwable = fapHubError,
                    onRetry = onRetry,
                    fapErrorSize = FapErrorSize.FULLSCREEN
                )
            }
        }
}
