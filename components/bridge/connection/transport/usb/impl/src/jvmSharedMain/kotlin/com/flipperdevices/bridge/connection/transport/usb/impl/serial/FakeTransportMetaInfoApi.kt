package com.flipperdevices.bridge.connection.transport.usb.impl.serial

import com.flipperdevices.bridge.connection.transport.common.api.meta.FTransportMetaInfoApi
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeTransportMetaInfoApi : FTransportMetaInfoApi {
    override fun get(key: TransportMetaInfoKey): Result<Flow<ByteArray?>> {
        return Result.success(flowOf(getData(key)))
    }

    private fun getData(key: TransportMetaInfoKey): ByteArray {
        return when (key) {
            TransportMetaInfoKey.DEVICE_NAME -> "USB Flipper".toByteArray()
            TransportMetaInfoKey.MANUFACTURER -> "Flipper Devices Inc.".toByteArray()
            TransportMetaInfoKey.HARDWARE_VERSION -> "11".toByteArray()
            TransportMetaInfoKey.SOFTWARE_VERSION -> "1.0.0 fakeusb0".toByteArray()
            TransportMetaInfoKey.API_VERSION -> "0.24".toByteArray()
            TransportMetaInfoKey.BATTERY_LEVEL -> byteArrayOf(0.5.toInt().toByte())
            TransportMetaInfoKey.BATTERY_POWER_STATE -> byteArrayOf(0)
        }
    }
}