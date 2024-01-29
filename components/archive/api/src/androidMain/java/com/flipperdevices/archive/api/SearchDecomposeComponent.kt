package com.flipperdevices.archive.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class SearchDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemSelected: SelectKeyPathListener?,
            onBack: DecomposeOnBackParameter
        ): SearchDecomposeComponent<*>
    }
}

fun interface SelectKeyPathListener {
    operator fun invoke(keyPath: FlipperKeyPath)
}
