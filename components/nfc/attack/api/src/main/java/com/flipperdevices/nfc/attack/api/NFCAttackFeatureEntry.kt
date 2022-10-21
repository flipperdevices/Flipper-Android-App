package com.flipperdevices.nfc.attack.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.FeatureScreenRootRoute

interface NFCAttackFeatureEntry : AggregateFeatureEntry {
    override val ROUTE: FeatureScreenRootRoute
        get() = FeatureScreenRootRoute.NFC_ATTACKS

    fun start(): String
}
