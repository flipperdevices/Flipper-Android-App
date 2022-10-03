package com.flipperdevices.widget.api

import com.github.terrakok.cicerone.Screen

interface WidgetApi {
    fun getWidgetOptionsScreen(widgetId: Int): Screen
}
