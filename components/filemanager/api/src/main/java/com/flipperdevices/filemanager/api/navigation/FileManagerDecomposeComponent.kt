package com.flipperdevices.filemanager.api.navigation

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent

interface FileManagerDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): FileManagerDecomposeComponent
    }
}
