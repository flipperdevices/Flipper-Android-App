package com.flipperdevices.keyscreen.api

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface KeyScreenFeatureEntry : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.KEY_SCREEN

    fun getKeyScreen(keyPath: FlipperKeyPath): String
}
