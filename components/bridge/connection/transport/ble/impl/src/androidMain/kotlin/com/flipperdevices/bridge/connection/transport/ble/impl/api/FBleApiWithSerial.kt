package com.flipperdevices.bridge.connection.transport.ble.impl.api

import com.flipperdevices.bridge.connection.transport.ble.api.GATTCharacteristicAddress
import com.flipperdevices.bridge.connection.transport.ble.impl.serial.FSerialRestartApiImpl
import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.common.api.meta.TransportMetaInfoKey
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

class FBleApiWithSerial(
    scope: CoroutineScope,
    client: ClientBleGatt,
    metaInfoGattMap: ImmutableMap<TransportMetaInfoKey, GATTCharacteristicAddress>,
    statusListener: FTransportConnectionStatusListener,
    serialDeviceApi: FSerialDeviceApi,
    serialRestartApi: FSerialRestartApiImpl,
) : FBleApiImpl(scope, client, statusListener, metaInfoGattMap),
    FSerialDeviceApi by serialDeviceApi,
    FSerialRestartApi by serialRestartApi
