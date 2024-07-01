package com.flipperdevices.bridge.service.impl.delegate.connection

import com.flipperdevices.bridge.service.impl.model.SavedFlipperConnectionInfo

interface FlipperConnectionDelegate {
    suspend fun connect(connectionInfo: SavedFlipperConnectionInfo): Boolean
}