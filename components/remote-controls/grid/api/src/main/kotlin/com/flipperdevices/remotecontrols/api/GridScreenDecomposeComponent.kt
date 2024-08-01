package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class GridScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: Param,
            onPopClick: () -> Unit
        ): GridScreenDecomposeComponent
    }

    sealed interface Param {
        data class Id(val irFileId: Long) : Param
        data class Path(val flipperKeyPath: FlipperKeyPath) : Param

        val key: String
            get() = this.toString()
    }
}
