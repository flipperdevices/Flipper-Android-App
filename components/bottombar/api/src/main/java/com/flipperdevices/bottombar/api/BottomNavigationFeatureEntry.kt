package com.flipperdevices.bottombar.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.deeplink.model.Deeplink

interface BottomNavigationFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.MAIN_BOTTOM_NAVIGATION

    fun start(deeplink: Deeplink? = null): String
}
