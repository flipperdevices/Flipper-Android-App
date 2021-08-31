package com.flipper.bridge.api.scanner

import com.flipper.bridge.models.BLEDevice
import kotlinx.coroutines.flow.Flow

interface FlipperScanner {
    fun findFlipperDevices(): Flow<Iterable<BLEDevice>>
}