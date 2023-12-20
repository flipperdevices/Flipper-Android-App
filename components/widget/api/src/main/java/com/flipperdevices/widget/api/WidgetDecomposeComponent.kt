package com.flipperdevices.widget.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface WidgetDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            widgetId: Int
        ): WidgetDecomposeComponent
    }
}
