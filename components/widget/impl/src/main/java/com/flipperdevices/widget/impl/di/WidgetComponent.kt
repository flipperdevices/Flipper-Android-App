package com.flipperdevices.widget.impl.di

import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.bridge.service.api.provider.FlipperServiceProvider
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyscreen.api.EmulateHelper
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.InvalidateWidgetsTask
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface WidgetComponent {
    val invalidateWidgetsTask: InvalidateWidgetsTask
    val widgetDataApi: WidgetDataApi
    val emulateHelper: EmulateHelper
    val serviceProvider: FlipperServiceProvider
    val widgetStateStorage: WidgetStateStorage
}
