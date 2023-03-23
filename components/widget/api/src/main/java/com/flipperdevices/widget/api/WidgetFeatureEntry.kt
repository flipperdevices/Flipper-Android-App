package com.flipperdevices.widget.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.deeplink.model.Deeplink

interface WidgetFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.WIDGET

    fun getWidgetScreenByDeeplink(deeplink: Deeplink): String
}
