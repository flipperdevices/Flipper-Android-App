package com.flipperdevices.debug.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute


interface StressTestFeatureEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.STRESS_TEST
}
