package com.flipperdevices.filemanager.main.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class FileManagerDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onBack: DecomposeOnBackParameter,
        ): FileManagerDecomposeComponent<*>
    }
}
