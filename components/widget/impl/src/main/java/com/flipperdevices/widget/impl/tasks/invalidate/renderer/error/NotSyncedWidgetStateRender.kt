package com.flipperdevices.widget.impl.tasks.invalidate.renderer.error

import android.content.Context
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ApplicationParams
import com.flipperdevices.keyscreen.api.DeepLinkOpenKey
import com.flipperdevices.widget.impl.R
import com.flipperdevices.widget.impl.model.WidgetRendererOf
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.tasks.invalidate.renderer.WidgetStateRenderer
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@WidgetRendererOf(WidgetState.ERROR_NOT_SYNCED)
@ContributesMultibinding(AppGraph::class, WidgetStateRenderer::class)
class NotSyncedWidgetStateRender @Inject constructor(
    context: Context,
    deepLinkOpenKey: DeepLinkOpenKey,
    applicationParams: ApplicationParams
) : ConfigureErrorWidgetStateRenderer(
    context,
    applicationParams,
    deepLinkOpenKey,
    R.string.widget_err_not_synced
) {
    override val TAG = "NotSyncedWidgetStateRender"
}
