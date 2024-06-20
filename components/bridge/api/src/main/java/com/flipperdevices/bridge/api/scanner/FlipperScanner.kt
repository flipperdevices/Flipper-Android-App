package com.flipperdevices.bridge.api.scanner

import kotlinx.coroutines.flow.Flow

/**
 * A class that helps you scan your surroundings for flippers.
 */
interface FlipperScanner {
    /**
     * @return only flipper devices as flow list. Remember previous remembered devices
     */
    fun findFlipperDevices(): Flow<Iterable<DiscoveredBluetoothDevice>>

    /**
     * @return flipper by id
     */
    suspend fun findFlipperById(deviceId: String): Flow<DiscoveredBluetoothDevice>

    /**
     * @return flipper by id
     */
    fun findFlipperByName(deviceName: String): Flow<DiscoveredBluetoothDevice>
}
