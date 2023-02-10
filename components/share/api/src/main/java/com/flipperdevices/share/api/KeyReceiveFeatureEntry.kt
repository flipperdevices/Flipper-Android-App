package com.flipperdevices.share.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute
import com.flipperdevices.deeplink.model.Deeplink

interface KeyReceiveFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.KEY_RECEIVE

    fun getKeyReceiveScreen(deeplink: Deeplink): String
}
