package com.flipperdevices.filemanager.api.navigation

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent

abstract class FileManagerDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): FileManagerDecomposeComponent<*>
    }
}
