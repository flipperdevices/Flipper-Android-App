package com.flipperdevices.widget.impl.di

import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.widget.impl.providers.BaseWidgetProvider
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.StartEmulateWorker
import com.flipperdevices.widget.impl.tasks.StopEmulateWorker
import com.flipperdevices.widget.impl.tasks.WaitForEmulateEndWorker
import com.flipperdevices.widget.impl.tasks.WaitingForFlipperConnectWorker
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsHelper
import com.flipperdevices.widget.impl.tasks.invalidate.InvalidateWidgetsWorker
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface WidgetComponent {
    val widgetDataApi: WidgetDataApi
    val widgetStateStorage: WidgetStateStorage
    val invalidateWidgetsHelper: InvalidateWidgetsHelper

    fun inject(worker: StartEmulateWorker)
    fun inject(worker: StopEmulateWorker)
    fun inject(worker: InvalidateWidgetsWorker)
    fun inject(worker: WaitForEmulateEndWorker)
    fun inject(worker: WaitingForFlipperConnectWorker)
    fun inject(provider: BaseWidgetProvider)
}
