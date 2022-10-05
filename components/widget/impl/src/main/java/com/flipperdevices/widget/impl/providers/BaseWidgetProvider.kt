package com.flipperdevices.widget.impl.providers

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.flipperdevices.bridge.dao.api.model.WidgetType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.widget.impl.di.WidgetComponent

abstract class BaseWidgetProvider(
    private val widgetType: WidgetType
) : AppWidgetProvider(), LogTagProvider {
    override val TAG = "BaseWidgetProvider"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        runBlockingWithLog("update") {
            ComponentHolder.component<WidgetComponent>().invalidateWidgetsTask.invoke()
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        runBlockingWithLog("deleted") {
            val widgetDataApi = ComponentHolder.component<WidgetComponent>().widgetDataApi
            appWidgetIds?.forEach { id ->
                widgetDataApi.deleteWidget(id)
            }
        }
    }
}