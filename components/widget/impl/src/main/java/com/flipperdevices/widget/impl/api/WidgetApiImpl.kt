package com.flipperdevices.widget.impl.api

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.api.WidgetApi
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsHelper
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding

@ContributesBinding(AppGraph::class, binding<WidgetApi>())
class WidgetApiImpl @Inject constructor(
    private val invalidateWidgetsHelper: InvalidateWidgetsHelper,
    private val updateStorage: WidgetStateStorage
) : WidgetApi {
    override suspend fun invalidate() {
        invalidateWidgetsHelper.invoke()
    }

    override suspend fun resetStateOfWidget(widgetId: Int) {
        updateStorage.updateState(widgetId, WidgetState.PENDING)
        invalidate()
    }
}
