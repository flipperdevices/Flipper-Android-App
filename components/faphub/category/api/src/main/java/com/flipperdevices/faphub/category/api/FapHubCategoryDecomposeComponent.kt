package com.flipperdevices.faphub.category.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter

abstract class FapHubCategoryDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            category: FapCategory,
            onBack: DecomposeOnBackParameter,
        ): FapHubCategoryDecomposeComponent<*>
    }
}
