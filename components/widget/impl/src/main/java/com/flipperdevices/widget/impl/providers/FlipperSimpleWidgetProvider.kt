package com.flipperdevices.widget.impl.providers

import android.appwidget.AppWidgetManager
import android.content.Context
import com.flipperdevices.bridge.dao.api.model.WidgetType

class FlipperSimpleWidgetProvider : BaseWidgetProvider(WidgetType.SIMPLE) {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }
}
