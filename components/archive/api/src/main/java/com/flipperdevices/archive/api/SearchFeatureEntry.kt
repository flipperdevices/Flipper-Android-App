package com.flipperdevices.archive.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface SearchFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.SEARCH_ARCHIVE

    fun getSearchScreen(isExitOnOpenKey: Boolean): String
}
