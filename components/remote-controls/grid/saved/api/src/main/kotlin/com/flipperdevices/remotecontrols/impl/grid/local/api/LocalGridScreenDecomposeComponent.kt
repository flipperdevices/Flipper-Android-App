package com.flipperdevices.remotecontrols.impl.grid.local.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class LocalGridScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            keyPath: FlipperKeyPath,
            onBack: DecomposeOnBackParameter,
            onCallback: (Callback) -> Unit
        ): LocalGridScreenDecomposeComponent
    }

    sealed interface Callback {
        data object UiFileNotFound : Callback
        data object Deleted : Callback
        data class Rename(val keyPath: FlipperKeyPath) : Callback
        data class ViewRemoteInfo(val keyPath: FlipperKeyPath) : Callback
    }
}
