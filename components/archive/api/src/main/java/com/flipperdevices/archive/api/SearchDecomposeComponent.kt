package com.flipperdevices.archive.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.DecomposeComponent

interface SearchDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemSelected: SelectKeyPathListener?
        ): SearchDecomposeComponent
    }
}

fun interface SelectKeyPathListener {
    operator fun invoke(keyPath: FlipperKeyPath)
}
