package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class GridCompositeDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: GridControlParam,
            onBack: DecomposeOnBackParameter
        ): GridCompositeDecomposeComponent<*>
    }
}
