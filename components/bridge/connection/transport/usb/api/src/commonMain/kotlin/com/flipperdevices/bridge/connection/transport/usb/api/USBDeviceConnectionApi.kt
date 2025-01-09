package com.flipperdevices.bridge.connection.transport.usb.api

import com.flipperdevices.bridge.connection.transport.common.api.DeviceConnectionApi
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import kotlinx.coroutines.CoroutineScope

interface USBDeviceConnectionApi : DeviceConnectionApi<FUSBApi, FUSBDeviceConnectionConfig> {
    override suspend fun connect(
        scope: CoroutineScope,
        config: FUSBDeviceConnectionConfig,
        listener: FTransportConnectionStatusListener
    ): Result<FUSBApi>
}
