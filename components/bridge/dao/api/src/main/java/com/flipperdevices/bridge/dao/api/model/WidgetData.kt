package com.flipperdevices.bridge.dao.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WidgetData(
    val widgetId: Int,
    val flipperKeyPath: FlipperKeyPath?,
    val widgetType: WidgetType
) : Parcelable

enum class WidgetType {
    SIMPLE,
    ONE_CLICK
}
