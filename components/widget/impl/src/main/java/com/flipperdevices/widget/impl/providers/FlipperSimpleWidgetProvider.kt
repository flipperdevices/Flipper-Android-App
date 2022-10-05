package com.flipperdevices.widget.impl.providers

import android.appwidget.AppWidgetManager
import android.content.Context
import com.flipperdevices.bridge.dao.api.model.WidgetType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.widget.impl.di.WidgetComponent

class FlipperSimpleWidgetProvider : BaseWidgetProvider(WidgetType.SIMPLE) {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        runBlockingWithLog("update_simple") {
            val widgetDataApi = ComponentHolder.component<WidgetComponent>().widgetDataApi
            appWidgetIds.forEach {
                widgetDataApi.updateTypeForWidget(it, WidgetType.SIMPLE)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}
