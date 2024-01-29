package com.flipperdevices.bridge.dao.api.delegates

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.WidgetData
import com.flipperdevices.bridge.dao.api.model.WidgetType

interface WidgetDataApi {
    suspend fun getAll(): List<WidgetData>
    suspend fun getWidgetDataByWidgetId(widgetId: Int): WidgetData?

    suspend fun updateTypeForWidget(
        widgetId: Int,
        type: WidgetType
    )

    suspend fun updateKeyForWidget(
        widgetId: Int,
        flipperKeyPath: FlipperKeyPath
    )

    suspend fun deleteWidget(
        widgetId: Int
    )
}
