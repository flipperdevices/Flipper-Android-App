package com.flipperdevices.wearable.setup.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry

interface SetupApi : AggregateFeatureEntry {
    fun start(): String

    companion object {
        const val ROUTE = "@wearossetup"
    }
}
