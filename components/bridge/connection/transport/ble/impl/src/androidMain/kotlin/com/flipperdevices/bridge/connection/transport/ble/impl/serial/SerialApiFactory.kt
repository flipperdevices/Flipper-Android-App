package com.flipperdevices.bridge.connection.transport.ble.impl.serial

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices
import javax.inject.Inject

class SerialApiFactory @Inject constructor(
    private val unsafeApiImplFactory: FSerialUnsafeApiImpl.Factory,
    private val throttlerApiFactory: FSerialOverflowThrottler.Factory,
) : LogTagProvider {
    override val TAG = "SerialApiCombinedFactory"
    fun build(
        config: FBleDeviceSerialConfig,
        services: ClientBleGattServices,
        scope: CoroutineScope,
        flipperActionNotifier: FlipperActionNotifier
    ): FSerialDeviceApi? {
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
        var deviceApi: FSerialDeviceApi = unsafeApiImplFactory(
            rxCharacteristic = rxCharacteristic,
            txCharacteristic = txCharacteristic,
            scope = scope,
            flipperActionNotifier = flipperActionNotifier
        )

        val overflowControlConfig = config.overflowControl
        if (overflowControlConfig != null) {
            val overflowCharacteristic = serialService.findCharacteristic(
                overflowControlConfig.overflowServiceUuid
            )
            if (overflowCharacteristic == null) {
                info { "Can't build unsafe serial api, because can't find overflow characteristic " }
                return null
            }
            deviceApi = throttlerApiFactory(
                serialApi = deviceApi,
                scope = scope,
                overflowCharacteristic = overflowCharacteristic,
                flipperActionNotifier = flipperActionNotifier
            )
        }

        return deviceApi
    }
}
