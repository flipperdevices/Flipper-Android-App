package com.flipperdevices.wearable.sync.wear.api

import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry

interface KeysListApi : AggregateFeatureEntry {
    fun start(): String

    companion object {
        const val ROUTE = "@wearoskeyslist"
    }
}
