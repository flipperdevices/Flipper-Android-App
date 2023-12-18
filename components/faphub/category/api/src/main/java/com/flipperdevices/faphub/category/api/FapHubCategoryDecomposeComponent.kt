package com.flipperdevices.faphub.category.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.ui.decompose.DecomposeComponent

interface FapHubCategoryDecomposeComponent : DecomposeComponent {

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            category: FapCategory
        ): FapHubCategoryDecomposeComponent
    }
}
