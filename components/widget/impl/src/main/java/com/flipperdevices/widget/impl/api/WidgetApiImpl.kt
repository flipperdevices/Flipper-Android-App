package com.flipperdevices.widget.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.api.WidgetApi
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsHelper
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, WidgetApi::class)
class WidgetApiImpl @Inject constructor(
    private val invalidateWidgetsHelper: InvalidateWidgetsHelper
) : WidgetApi {
    override suspend fun invalidate() {
        invalidateWidgetsHelper.invoke()
    }
}
