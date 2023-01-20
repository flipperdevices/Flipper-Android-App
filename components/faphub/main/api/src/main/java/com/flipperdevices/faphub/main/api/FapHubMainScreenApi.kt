package com.flipperdevices.faphub.main.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface FapHubMainScreenApi : AggregateFeatureEntry {
    override val ROUTE
        get() = FeatureScreenRootRoute.FAP_HUB_MAIN
}
