package com.flipperdevices.widget.impl.tasks.invalidate

import android.appwidget.AppWidgetManager
import android.content.Context
import com.flipperdevices.bridge.dao.api.delegates.WidgetDataApi
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ktx.jre.withLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.widget.impl.model.WidgetState
import com.flipperdevices.widget.impl.storage.WidgetStateStorage
import com.flipperdevices.widget.impl.tasks.invalidate.renderer.WidgetStateRenderer
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.sync.Mutex

interface InvalidateWidgetsHelper {
    suspend fun invoke()
}

@Singleton
@ContributesBinding(AppGraph::class, InvalidateWidgetsHelper::class)
class InvalidateWidgetHelperImpl @Inject constructor(
    private val widgetDataApi: WidgetDataApi,
    private val context: Context,
    private val rendererMap: MutableMap<WidgetState, WidgetStateRenderer>,
    private val widgetStateStorage: WidgetStateStorage
) : InvalidateWidgetsHelper, LogTagProvider {
    override val TAG = "InvalidateWidgetsHelper"
    private val mutex = Mutex()

    private val appWidgetManager by lazy { AppWidgetManager.getInstance(context) }

    override suspend fun invoke() = withLock(mutex, "invalidate") {
        info { "Invoke invalidate widgets" }
        val widgets = widgetDataApi.getAll()
        info { "Receive $widgets" }
        widgets.forEach {
            info { "Invalidate for $it widget" }
            val keyPath = it.flipperKeyPath
            val state = if (keyPath != null) {
                widgetStateStorage.getState(it.widgetId)
            } else WidgetState.NOT_INITIALIZE
            info { "Try render ${it.widgetId} for state: $state" }
            val view = rendererMap[state]?.render(it.widgetId, keyPath)
            if (view == null) {
                error { "Can't build view for $it" }
                return@forEach
            }
            appWidgetManager.updateAppWidget(it.widgetId, view)
        }
    }
}
