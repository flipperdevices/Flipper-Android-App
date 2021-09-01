package com.flipper.bridge.api.scanner

import com.flipper.core.models.BLEDevice
import kotlinx.coroutines.flow.Flow

interface FlipperScanner {
    fun findFlipperDevices(): Flow<Iterable<BLEDevice>>
}