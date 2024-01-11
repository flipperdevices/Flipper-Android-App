package com.flipperdevices.settings.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent

abstract class SettingsDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): SettingsDecomposeComponent<*>
    }
}
