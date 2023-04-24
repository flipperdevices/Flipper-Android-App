package com.flipperdevices.infrared.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface InfraredFeatureEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.INFRARED
}
