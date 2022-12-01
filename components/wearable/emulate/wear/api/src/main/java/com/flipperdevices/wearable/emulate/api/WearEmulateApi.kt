package com.flipperdevices.wearable.emulate.api

import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface WearEmulateApi : ComposableFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.WEAR_EMULATE

    fun open(path: String): String
}
