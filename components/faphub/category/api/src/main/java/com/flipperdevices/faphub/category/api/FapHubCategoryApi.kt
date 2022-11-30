package com.flipperdevices.faphub.category.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.faphub.dao.api.model.FapCategory

interface FapHubCategoryApi : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.FAP_HUB_CATEGORY

    fun open(category: FapCategory): String
}