package com.flipperdevices.bridge.connection.ble.impl.serial

import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

class SerialApiFactory @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    private val unsafeApiImplFactory: FSerialUnsafeApiImpl.Factory
) : LogTagProvider {
    override val TAG = "SerialApiCombinedFactory"
    fun build(
        config: FBleDeviceSerialConfig,
        services: ClientBleGattServices
    ): FSerialDeviceApi? {
        var deviceApi: FSerialDeviceApi?

        val serialService = services.findService(config.serialServiceUuid)
        val rxCharacteristic = serialService?.findCharacteristic(config.rxServiceCharUuid)
        val txCharacteristic = serialService?.findCharacteristic(config.txServiceCharUuid)
        if (rxCharacteristic == null || txCharacteristic == null) {
            error {
                "Can't build unsafe serial api, because can't find characteristic " +
                        "for config $config and services $services"
            }
            return null
        }
        deviceApi = unsafeApiImplFactory(
            rxCharacteristic = rxCharacteristic,
            txCharacteristic = txCharacteristic,
            scope = scope
        )

        return deviceApi
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope
        ): SerialApiFactory
    }
}