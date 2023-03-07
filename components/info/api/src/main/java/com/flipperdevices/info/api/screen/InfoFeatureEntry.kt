package com.flipperdevices.info.api.screen

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.deeplink.model.Deeplink

interface InfoFeatureEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.DEVICE_INFO

    fun fullInfo(): String

    fun getWebUpdateByDeeplink(deeplink: Deeplink): String
}
