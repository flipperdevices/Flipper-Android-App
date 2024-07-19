package com.flipperdevices.widget.impl.providers

import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import com.flipperdevices.bridge.dao.api.model.WidgetType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.android.toFullString
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.widget.impl.di.WidgetComponent

abstract class BaseWidgetProvider(
    private val widgetType: WidgetType
) : AppWidgetProvider(), LogTagProvider {
    override val TAG = "BaseWidgetProvider-$widgetType"

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        info { "Call ${intent?.toFullString()} for $widgetType" }
        runBlockingWithLog("update") {
            ComponentHolder.component<WidgetComponent>().invalidateWidgetsHelper.invoke()
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        info { "Deleted ${appWidgetIds?.toList()}" }
        runBlockingWithLog("deleted") {
            val widgetDataApi = ComponentHolder.component<WidgetComponent>().widgetDataApi
            appWidgetIds?.forEach { id ->
                widgetDataApi.deleteWidget(id)
            }
        }
    }
}
