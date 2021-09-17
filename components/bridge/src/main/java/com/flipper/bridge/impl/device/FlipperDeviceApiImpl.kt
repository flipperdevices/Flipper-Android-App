package com.flipper.bridge.impl.device

import com.flipper.bridge.api.device.FlipperDeviceApi
import com.flipper.bridge.impl.manager.FlipperBleManager

class FlipperDeviceApiImpl(
    private val bleManager: FlipperBleManager,
    override val address: String
) : FlipperDeviceApi {
    override fun getBleManager(): FlipperBleManager {
        return bleManager
    }
}
