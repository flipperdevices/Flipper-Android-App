package com.flipperdevices.core.ui.errors.impl.api

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.bottombar.api.BottomNavigationHandleDeeplink
import com.flipperdevices.bottombar.model.BottomBarTab
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.errors.impl.ComposableThrowableErrorInternal
import com.flipperdevices.faphub.errors.api.FapErrorSize
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapHubComposableErrorsRenderer::class)
class FapHubComposableErrorsRendererImpl @Inject constructor(
    private val bottomNavigationHandleDeeplink: BottomNavigationHandleDeeplink
) : FapHubComposableErrorsRenderer {

    override fun LazyListScope.ComposableThrowableErrorListItem(
        throwable: Throwable,
        onRetry: () -> Unit,
        modifier: Modifier,
        fapErrorSize: FapErrorSize
    ) {
        item {
            var throwableModifier = modifier

            if (fapErrorSize == FapErrorSize.FULLSCREEN) {
                throwableModifier = throwableModifier.fillParentMaxHeight()
            }

            ComposableThrowableError(
                throwable = throwable,
                onRetry = onRetry,
                modifier = throwableModifier,
                fapErrorSize = fapErrorSize
            )
        }
    }

    @Composable
    override fun ComposableThrowableError(
        throwable: Throwable,
        onRetry: () -> Unit,
        modifier: Modifier,
        fapErrorSize: FapErrorSize
    ) {
        ComposableThrowableErrorInternal(
            throwable = throwable,
            onRetry = onRetry,
            modifier = modifier,
            fapErrorSize = fapErrorSize,
            onOpenDeviceScreen = {
                bottomNavigationHandleDeeplink.onChangeTab(BottomBarTab.DEVICE, force = true)
            }
        )
    }
}
