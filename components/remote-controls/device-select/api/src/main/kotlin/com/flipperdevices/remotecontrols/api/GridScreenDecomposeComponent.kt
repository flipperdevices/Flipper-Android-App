package com.flipperdevices.remotecontrols.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent

abstract class GridScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {

    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
            param: Param,
            onPopClicked: ()-> Unit
        ): GridScreenDecomposeComponent
    }

    class Param(
        val ifrFileId: Long,
        val uiFileId: Long? = null
    )
}