package com.flipperdevices.changelog.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import com.flipperdevices.updater.model.UpdateRequest

abstract class ChangelogScreenDecomposeComponent(
    componentContext: ComponentContext
) : ScreenDecomposeComponent(componentContext) {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            updateRequest: UpdateRequest,
            onBack: DecomposeOnBackParameter
        ): ChangelogScreenDecomposeComponent
    }
}
