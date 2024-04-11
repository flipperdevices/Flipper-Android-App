package com.flipperdevices.bridge.connection.ble.impl

import com.flipperdevices.bridge.connection.ble.api.FBleApi
import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.ble.impl.serial.FSerialDeviceApiWrapper
import com.flipperdevices.bridge.connection.common.api.FInternalTransportConnectionStatus
import com.flipperdevices.bridge.connection.common.api.FTransportConnectionStatusListener
import com.flipperdevices.bridge.connection.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.kotlin.ble.client.main.callback.ClientBleGatt
import no.nordicsemi.android.kotlin.ble.core.data.GattConnectionState

open class FBleApiImpl(
    scope: CoroutineScope,
    private val client: ClientBleGatt,
    private val statusListener: FTransportConnectionStatusListener,
) : FBleApi, LogTagProvider {
    override val TAG = "FBleApi"
    init {
        info { "Init ble api listener" }
        client.connectionStateWithStatus
            .filterNotNull()
            .onEach { (state, status) ->
                info { "Receive state $state" }
                statusListener.onStatusUpdate(
                    when (state) {
                        GattConnectionState.STATE_DISCONNECTED ->
                            FInternalTransportConnectionStatus.Disconnected

                        GattConnectionState.STATE_CONNECTING ->
                            FInternalTransportConnectionStatus.Connecting

                        GattConnectionState.STATE_CONNECTED ->
                            FInternalTransportConnectionStatus.Connected

                        GattConnectionState.STATE_DISCONNECTING ->
                            FInternalTransportConnectionStatus.Disconnecting
                    }
                )
            }.launchIn(scope)
    }
    override suspend fun disconnect() {
        client.disconnect()
        client.close()
    }
}

open class FBleApiWithSerial @AssistedInject constructor(
    @Assisted private val scope: CoroutineScope,
    @Assisted private val client: ClientBleGatt,
    @Assisted private val config: FBleDeviceSerialConfig,
    @Assisted private val statusListener: FTransportConnectionStatusListener,
    serialDeviceApiWrapperFactory: FSerialDeviceApiWrapper.Factory
) : FBleApiImpl(scope, client, statusListener),
    FSerialDeviceApi by serialDeviceApiWrapperFactory(
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
            statusListener: FTransportConnectionStatusListener
        ): FBleApiWithSerial
    }
}
