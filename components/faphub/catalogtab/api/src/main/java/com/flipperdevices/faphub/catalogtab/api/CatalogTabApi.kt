package com.flipperdevices.faphub.catalogtab.api

import androidx.compose.runtime.Composable
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItem

interface CatalogTabApi {
    @Composable
    fun ComposableCatalogTab(
        onOpenFapItem: (FapItem) -> Unit,
        onCategoryClick: (FapCategory) -> Unit
    )
}
