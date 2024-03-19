package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.ble.api.FBleApi
import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.ble.impl.serial.FSerialDeviceApiWrapper
import com.flipperdevices.bridge.connection.common.api.serial.FSerialDeviceApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt

open class FBleApiImpl(
) : FBleApi {
}

open class FBleApiWithSerial @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val client: ClientBleGatt,
    @Assisted private val config: FBleDeviceSerialConfig,
    serialDeviceApiWrapperFactory: FSerialDeviceApiWrapper.Factory
) : FBleApiImpl(), FSerialDeviceApi by serialDeviceApiWrapperFactory(
    scope = scope,
    config = config,
    services = client.services
) {
    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
            client: ClientBleGatt,
            config: FBleDeviceSerialConfig,
        ): FBleApiWithSerial
    }
}