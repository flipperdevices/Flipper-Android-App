package com.flipperdevices.updater.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ui.decompose.DecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.updater.model.UpdateRequest

interface UpdaterDecomposeComponent : DecomposeComponent {
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            updateRequest: UpdateRequest?,
            onBack: DecomposeOnBackParameter
        ): UpdaterDecomposeComponent
    }
}
