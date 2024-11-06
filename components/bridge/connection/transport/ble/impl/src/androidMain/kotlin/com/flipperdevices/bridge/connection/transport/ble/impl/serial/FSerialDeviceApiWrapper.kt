package com.flipperdevices.bridge.connection.transport.ble.impl.serial

import com.flipperdevices.bridge.connection.feature.actionnotifier.api.FlipperActionNotifier
import com.flipperdevices.bridge.connection.transport.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialDeviceApi
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.WaitNotifyLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.log.warn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

class FSerialDeviceApiWrapper @AssistedInject constructor(
    @Assisted scope: CoroutineScope,
    @Assisted config: FBleDeviceSerialConfig,
    @Assisted serviceFlow: StateFlow<ClientBleGattServices?>,
    @Assisted private val flipperActionNotifier: FlipperActionNotifier,
    serialApiFactory: SerialApiFactory
) : FSerialDeviceApi, LogTagProvider {
    override val TAG = "FSerialDeviceApiWrapper"
    private var serialApiScope: CoroutineScope? = null
    private var delegateSerialApi: FSerialDeviceApi? = null
    private val lock = WaitNotifyLock()

    override fun getActionNotifier(): FlipperActionNotifier = flipperActionNotifier

    init {
        scope.launch {
            serviceFlow.collect { services ->
                info { "Create new serial api because $services changed" }

                serialApiScope?.cancel()
                val newSerialApiScope = CoroutineScope(
                    FlipperDispatchers.workStealingDispatcher + SupervisorJob(scope.coroutineContext.job)
                ).also { serialApiScope = it }

                if (services == null) {
                    delegateSerialApi = null
                    info { "Set serial api to null because service is null, $services" }
                } else {
                    val serialApi = serialApiFactory.build(
                        config = config,
                        services = services,
                        scope = newSerialApiScope,
                        flipperActionNotifier = flipperActionNotifier
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

    override suspend fun getReceiveBytesFlow() = waitForSerialApi().getReceiveBytesFlow()

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
            services: StateFlow<ClientBleGattServices?>,
            flipperActionNotifier: FlipperActionNotifier
        ): FSerialDeviceApiWrapper
    }
}
