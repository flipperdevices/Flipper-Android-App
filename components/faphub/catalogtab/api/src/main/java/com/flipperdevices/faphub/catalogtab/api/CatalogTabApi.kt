package com.flipperdevices.faphub.catalogtab.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.dao.api.model.FapItemShort

@Immutable
interface CatalogTabApi {
    @Composable
    fun ComposableCatalogTab(
        onOpenFapItem: (FapItemShort) -> Unit,
        onCategoryClick: (FapCategory) -> Unit
    )
}
