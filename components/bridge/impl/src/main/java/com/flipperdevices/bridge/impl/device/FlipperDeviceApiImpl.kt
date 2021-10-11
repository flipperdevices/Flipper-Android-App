package com.flipperdevices.bridge.impl.device

import com.flipperdevices.bridge.api.device.FlipperDeviceApi
import com.flipperdevices.bridge.impl.manager.FlipperBleManagerImpl

class FlipperDeviceApiImpl(
    private val bleManagerImpl: FlipperBleManagerImpl,
    override val address: String
) : FlipperDeviceApi {
    override fun getBleManager(): FlipperBleManagerImpl {
        return bleManagerImpl
    }
}
