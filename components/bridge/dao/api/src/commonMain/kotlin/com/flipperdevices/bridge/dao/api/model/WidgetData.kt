package com.flipperdevices.bridge.dao.api.model

data class WidgetData(
    val widgetId: Int,
    val flipperKeyPath: FlipperKeyPath?,
    val widgetType: WidgetType
)

enum class WidgetType {
    SIMPLE,
    ONE_CLICK
}
