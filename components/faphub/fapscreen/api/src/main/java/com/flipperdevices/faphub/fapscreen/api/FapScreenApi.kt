package com.flipperdevices.faphub.fapscreen.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface FapScreenApi : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.FAP_HUB_APPSCREEN

    fun getFapScreen(id: String): String

    fun getFapScreenByDeeplink(id: String): String
}
