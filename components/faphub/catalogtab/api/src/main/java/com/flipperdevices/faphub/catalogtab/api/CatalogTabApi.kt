package com.flipperdevices.faphub.catalogtab.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort
import com.flipperdevices.faphub.screenshotspreview.api.ScreenshotsClickListener

@Immutable
interface CatalogTabApi {
    @Composable
    @Suppress("NonSkippableComposable")
    fun ComposableCatalogTab(
        componentContext: ComponentContext,
        onOpenFapItem: (FapItemShort) -> Unit,
        onCategoryClick: (FapCategory) -> Unit,
        screenshotsClickListener: ScreenshotsClickListener
    )
}
