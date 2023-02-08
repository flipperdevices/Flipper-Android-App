package com.flipperdevices.info.api.screen

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface InfoFeatureEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.DEVICE_INFO

    fun fullInfo(): String
}
