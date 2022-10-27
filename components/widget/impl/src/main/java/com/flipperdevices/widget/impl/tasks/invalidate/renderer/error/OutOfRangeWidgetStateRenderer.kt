package com.flipperdevices.widget.impl.tasks.invalidate.renderer.error

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.model.WidgetRendererOf
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.tasks.invalidate.renderer.WidgetStateRenderer
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@WidgetRendererOf(WidgetState.ERROR_OUT_OF_RANGE)
@ContributesMultibinding(AppGraph::class, WidgetStateRenderer::class)
class OutOfRangeWidgetStateRenderer @Inject constructor(
    context: Context,
    deepLinkOpenKey: DeepLinkOpenKey
) : RetryErrorWidgetStateRenderer(context, deepLinkOpenKey, R.string.widget_err_cant_connect) {
    override val TAG = "OutOfRangeWidgetStateRenderer"
}
