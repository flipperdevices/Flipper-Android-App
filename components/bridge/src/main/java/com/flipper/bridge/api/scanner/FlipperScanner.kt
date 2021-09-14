package com.flipper.bridge.api.scanner

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * A class that helps you scan your surroundings for flippers.
 */
interface FlipperScanner {
    /**
     * @return only flipper devices as flow list. Remember previous remembered devices
     */
    @ExperimentalCoroutinesApi
    fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>>

    /**
     * @return flipper by id
     */
    @ExperimentalCoroutinesApi
    fun findFlipperById(deviceId: String): Flow<DiscoveredBluetoothDevice>
}
