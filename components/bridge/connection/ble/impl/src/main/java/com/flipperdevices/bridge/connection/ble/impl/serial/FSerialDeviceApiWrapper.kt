package com.flipperdevices.bridge.connection.ble.impl.serial

import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.common.api.serial.FSerialDeviceApi
import com.flipperdevices.bridge.connection.common.api.serial.FlipperSerialSpeed
import com.flipperdevices.core.ktx.jre.WaitNotifyLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

class FSerialDeviceApiWrapper @AssistedInject constructor(
    @Assisted scope: CoroutineScope,
    @Assisted config: FBleDeviceSerialConfig,
    @Assisted serviceFlow: StateFlow<ClientBleGattServices?>,
    serialApiFactoryBuilder: SerialApiFactory.Factory
) : FSerialDeviceApi, LogTagProvider {
    override val TAG = "FSerialDeviceApiWrapper"
    private val serialApiFactory = serialApiFactoryBuilder(scope)
    private var delegateSerialApi: FSerialDeviceApi? = null
    private val receiveByteFlow = MutableSharedFlow<ByteArray>()
    private val lock = WaitNotifyLock()

    init {
        scope.launch {
            serviceFlow.collect { services ->
                val service = services?.findService(config.serialServiceUuid)
                if (service == null) {
                    delegateSerialApi = null
                    info { "Set serial api to null because service is null, $services" }
                } else {
                    val serialApi = serialApiFactory.build(
                        config, services
                    )
                    if (serialApi == null) {
                        delegateSerialApi = null
                        info { "Serial api by factory is null, so set delegate to null" }
                        return@collect
                    }
                    delegateSerialApi = serialApi
                    lock.notifyAll()
                }
            }
        }
    }


    override suspend fun getSpeed() = waitForSerialApi().getSpeed()

    override suspend fun getReceiveBytesFlow() = receiveByteFlow.asSharedFlow()

    override suspend fun sendBytes(data: ByteArray) {
        waitForSerialApi().sendBytes(data)
    }

    private suspend fun waitForSerialApi(): FSerialDeviceApi {
        var serialApi = delegateSerialApi
        while (serialApi == null) {
            warn { "Try get access to null delegate serial api, start waiting" }
            lock.wait()
            serialApi = delegateSerialApi
        }
        return serialApi
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            scope: CoroutineScope,
            config: FBleDeviceSerialConfig,
            services: StateFlow<ClientBleGattServices?>
        ): FSerialDeviceApiWrapper
    }
}