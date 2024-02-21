package com.flipperdevices.widget.screen.model

import com.flipperdevices.archive.api.SelectKeyPathListener
import kotlinx.serialization.Serializable

@Serializable
sealed class WidgetNavigationConfig {
    @Serializable
    data class WidgetOptions(val widgetId: Int) : WidgetNavigationConfig()

    @Serializable
    data class SearchScreen(val listener: SelectKeyPathListener) : WidgetNavigationConfig()
}
