package com.flipperdevices.widget.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent

abstract class WidgetDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            widgetId: Int
        ): WidgetDecomposeComponent<*>
    }
}
