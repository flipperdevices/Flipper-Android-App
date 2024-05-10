package com.flipperdevices.bridge.connection.transport.ble.impl.api

import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.transport.ble.impl.serial.FSerialDeviceApiWrapper
import com.flipperdevices.bridge.connection.transport.ble.impl.serial.FSerialRestartApiImpl
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import javax.inject.Inject

class FBleApiWithSerialFactory @Inject constructor(
    private val serialDeviceApiWrapperFactory: FSerialDeviceApiWrapper.Factory,
    private val fSerialRestartApiFactory: FSerialRestartApiImpl.Factory
) {
    fun build(
        scope: CoroutineScope,
        client: ClientBleGatt,
        config: FBleDeviceSerialConfig,
        statusListener: FTransportConnectionStatusListener
    ): FBleApiWithSerial {
        val serialDeviceApi = serialDeviceApiWrapperFactory(scope, config, client.services)
        val restartApi = fSerialRestartApiFactory(
            services = client.services,
            serialServiceUuid = config.serialServiceUuid,
            resetCharUUID = config.resetCharUUID
        )
        return FBleApiWithSerial(
            scope = scope,
            client = client,
            statusListener = statusListener,
            serialDeviceApi = serialDeviceApi,
            serialRestartApi = restartApi
        )
    }
}
