package com.flipperdevices.nfc.mfkey32.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface MfKey32ScreenEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.MFKEY32

    fun startDestination(): String

    fun getMfKeyScreenByDeeplink(): String
}
