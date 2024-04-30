package com.flipperdevices.bridge.connection.transport.ble.impl.api

import com.flipperdevices.bridge.connection.transport.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

class FBleApiWithSerial(
    scope: CoroutineScope,
    client: ClientBleGatt,
    statusListener: FTransportConnectionStatusListener,
    serialDeviceApi: FSerialDeviceApi
) : FBleApiImpl(scope, client, statusListener),
    FSerialDeviceApi by serialDeviceApi
