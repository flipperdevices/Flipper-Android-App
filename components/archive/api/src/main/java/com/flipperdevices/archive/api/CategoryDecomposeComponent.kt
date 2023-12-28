package com.flipperdevices.archive.api

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.ui.decompose.DecomposeComponent

interface CategoryDecomposeComponent : DecomposeComponent {

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            categoryType: CategoryType,
            deeplink: Deeplink.BottomBar.ArchiveTab.ArchiveCategory?
        ): CategoryDecomposeComponent
    }
}
