package com.flipperdevices.widget.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.api.WidgetApi
import com.flipperdevices.widget.impl.tasks.InvalidateWidgetsTask
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, WidgetApi::class)
class WidgetApiImpl @Inject constructor(
    private val invalidateWidgetsTask: InvalidateWidgetsTask
) : WidgetApi {
    override suspend fun invalidate() {
        invalidateWidgetsTask.invoke()
    }
}
