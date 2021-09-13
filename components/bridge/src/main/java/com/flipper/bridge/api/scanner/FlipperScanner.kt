package com.flipper.bridge.api.scanner

import kotlinx.coroutines.flow.Flow

interface FlipperScanner {
    fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>>
}
