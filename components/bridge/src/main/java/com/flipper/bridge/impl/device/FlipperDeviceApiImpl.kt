package com.flipper.bridge.impl.device

import com.flipper.bridge.api.device.FlipperDeviceApi
import com.flipper.bridge.impl.manager.FlipperBleManager

class FlipperDeviceApiImpl(
    private val bleManager: FlipperBleManager
) : FlipperDeviceApi {
    override fun getBleManager(): FlipperBleManager {
        return bleManager
    }
}
