package com.flipperdevices.widget.impl.di

import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.impl.tasks.InvalidateWidgetsTask
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface WidgetComponent {
    val invalidateWidgetsTask: InvalidateWidgetsTask
    val widgetDataApi: WidgetDataApi
}