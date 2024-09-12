package com.flipperdevices.bridge.connection.feature.storage.impl.utils

import com.flipperdevices.bridge.connection.feature.rpc.model.FlipperRequestPriority
import com.flipperdevices.bridge.connection.feature.storage.api.model.StorageRequestPriority

fun StorageRequestPriority.toRpc() = when (this) {
    StorageRequestPriority.RIGHT_NOW -> FlipperRequestPriority.RIGHT_NOW
    StorageRequestPriority.FOREGROUND -> FlipperRequestPriority.FOREGROUND
    StorageRequestPriority.DEFAULT -> FlipperRequestPriority.DEFAULT
    StorageRequestPriority.BACKGROUND -> FlipperRequestPriority.BACKGROUND
}
