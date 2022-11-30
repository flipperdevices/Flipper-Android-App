package com.flipperdevices.faphub.search.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface FapHubSearchEntryApi : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.FAP_HUB_SEARCH

    fun start(): String
}