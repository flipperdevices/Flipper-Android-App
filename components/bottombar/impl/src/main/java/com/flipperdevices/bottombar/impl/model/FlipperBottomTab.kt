package com.flipperdevices.bottombar.impl.model

import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

/**
 * Warning: this class is processed by Proguard
 */
enum class FlipperBottomTab(val startRoute: FeatureScreenRootRoute) {
    DEVICE(FeatureScreenRootRoute.DEVICE_INFO),
    ARCHIVE(FeatureScreenRootRoute.DEVICE_INFO),
    HUB(FeatureScreenRootRoute.DEVICE_INFO)
}
