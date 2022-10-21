package com.flipperdevices.widget.api

import com.github.terrakok.cicerone.Screen

interface WidgetScreenApi {
    fun getWidgetOptionsScreen(widgetId: Int): Screen
}
