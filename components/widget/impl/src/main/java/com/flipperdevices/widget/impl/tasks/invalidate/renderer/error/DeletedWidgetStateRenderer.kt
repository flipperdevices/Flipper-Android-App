package com.flipperdevices.widget.impl.tasks.invalidate.renderer.error

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.model.WidgetRendererOf
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.tasks.invalidate.renderer.WidgetStateRenderer
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@WidgetRendererOf(WidgetState.ERROR_KEY_DELETED)
@ContributesIntoMap(AppGraph::class, binding<WidgetStateRenderer>())
class DeletedWidgetStateRenderer @Inject constructor(
    context: Context,
    deepLinkOpenKey: DeepLinkOpenKey
) : RetryErrorWidgetStateRenderer(context, deepLinkOpenKey, R.string.widget_err_deleted) {
    override val TAG = "DeletedWidgetStateRender"
}
