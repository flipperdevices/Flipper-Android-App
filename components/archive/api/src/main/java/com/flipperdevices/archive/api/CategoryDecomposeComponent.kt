package com.flipperdevices.archive.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.ui.decompose.CompositeDecomposeComponent

abstract class CategoryDecomposeComponent<C : Any> : CompositeDecomposeComponent<C>() {

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            categoryType: CategoryType
        ): CategoryDecomposeComponent<*>
    }
}
