package com.flipper.bridge.api.device

import com.flipper.bridge.impl.manager.FlipperBleManager

/**
 * Provide API to Flipper Device
 * For get instance of this object, use {@link FlipperPairApi#connect}
 */
interface FlipperDeviceApi {
    val address: String
    fun getBleManager(): FlipperBleManager
}
