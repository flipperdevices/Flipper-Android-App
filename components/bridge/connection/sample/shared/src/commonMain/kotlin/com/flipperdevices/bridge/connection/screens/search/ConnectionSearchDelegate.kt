package com.flipperdevices.bridge.connection.screens.search

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface ConnectionSearchDelegate {
    fun getDevicesFlow(): StateFlow<ImmutableList<ConnectionSearchItem>>
    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
        ): ConnectionSearchDelegate
    }
}