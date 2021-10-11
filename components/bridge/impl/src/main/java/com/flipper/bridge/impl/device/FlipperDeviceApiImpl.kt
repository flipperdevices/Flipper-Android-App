package com.flipper.bridge.impl.device

import com.flipper.bridge.api.device.FlipperDeviceApi
import com.flipper.bridge.impl.manager.FlipperBleManagerImpl

class FlipperDeviceApiImpl(
    private val bleManagerImpl: FlipperBleManagerImpl,
    override val address: String
) : FlipperDeviceApi {
    override fun getBleManager(): FlipperBleManagerImpl {
        return bleManagerImpl
    }
}
