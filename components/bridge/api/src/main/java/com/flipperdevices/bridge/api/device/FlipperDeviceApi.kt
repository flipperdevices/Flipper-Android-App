package com.flipperdevices.bridge.api.device

import com.flipperdevices.bridge.api.manager.FlipperBleManager

/**
 * Provide API to Flipper Device
 * For get instance of this object, use {@link FlipperPairApi#connect}
 */
interface FlipperDeviceApi {
    val address: String
    fun getBleManager(): FlipperBleManager
}
