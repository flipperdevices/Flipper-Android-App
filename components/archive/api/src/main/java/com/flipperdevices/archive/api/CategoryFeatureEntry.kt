package com.flipperdevices.archive.api

import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface CategoryFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.CATEGORY_KEY_SCREEN

    fun getCategoryScreen(categoryType: CategoryType): String
}
