package com.flipperdevices.bridge.connection.transport.ble.impl.api

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.transport.ble.api.GATTCharacteristicAddress
import com.flipperdevices.bridge.connection.transport.ble.impl.serial.FSerialDeviceApiWrapper
import com.flipperdevices.bridge.connection.transport.ble.impl.serial.FSerialRestartApiImpl
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import javax.inject.Inject

class FBleApiWithSerialFactory @Inject constructor(
    private val serialDeviceApiWrapperFactory: FSerialDeviceApiWrapper.Factory,
    private val fSerialRestartApiFactory: FSerialRestartApiImpl.Factory,
    private val flipperActionNotifierFactory: FlipperActionNotifier.Factory
) {
    fun build(
        scope: CoroutineScope,
        client: ClientBleGatt,
        serialConfig: FBleDeviceSerialConfig,
        metaInfoGattMap: ImmutableMap<TransportMetaInfoKey, GATTCharacteristicAddress>,
        statusListener: FTransportConnectionStatusListener
    ): FBleApiWithSerial {
        val flipperActionNotifier = flipperActionNotifierFactory.invoke(scope)
        val serialDeviceApi = serialDeviceApiWrapperFactory(
            scope = scope,
            config = serialConfig,
            services = client.services,
            flipperActionNotifier = flipperActionNotifier
        )
        val restartApi = fSerialRestartApiFactory(
            services = client.services,
            serialServiceUuid = serialConfig.serialServiceUuid,
            resetCharUUID = serialConfig.resetCharUUID
        )
        return FBleApiWithSerial(
            scope = scope,
            client = client,
            metaInfoGattMap = metaInfoGattMap,
            statusListener = statusListener,
            serialDeviceApi = serialDeviceApi,
            serialRestartApi = restartApi,
        )
    }
}
