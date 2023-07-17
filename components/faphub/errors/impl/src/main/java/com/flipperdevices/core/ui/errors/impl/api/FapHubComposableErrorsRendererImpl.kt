package com.flipperdevices.core.ui.errors.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapHubComposableErrorsRenderer::class)
class FapHubComposableErrorsRendererImpl @Inject constructor() : FapHubComposableErrorsRenderer {
    @Composable
    override fun ComposableThrowableError(
        throwable: Throwable,
        onRetry: () -> Unit,
        modifier: Modifier
    ) {
        TODO("Not yet implemented")
    }
}
