package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class ConfigureGridDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: ServerRemoteControlParam,
            onBack: DecomposeOnBackParameter,
        ): ConfigureGridDecomposeComponent<*>
    }
}
