package com.flipperdevices.bridge.connection.ble.impl.serial

import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.common.api.FSerialDeviceApi
import com.flipperdevices.core.ktx.jre.WaitNotifyLock
import com.flipperdevices.core.ktx.jre.withLock
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

class FSerialDeviceApiWrapper @AssistedInject constructor(
    @Assisted scope: CoroutineScope,
    @Assisted config: FBleDeviceSerialConfig,
    @Assisted serviceFlow: StateFlow<ClientBleGattServices?>,
    unsafeApiImplFactory: FSerialUnsafeApiImpl.Factory
) : FSerialDeviceApi, LogTagProvider {
    override val TAG = "FSerialDeviceApiWrapper"
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
                    val rxCharacteristic = service.findCharacteristic(config.rxServiceCharUuid)
                    val txCharacteristic = service.findCharacteristic(config.txServiceCharUuid)
                    if (rxCharacteristic == null || txCharacteristic == null) {
                        delegateSerialApi = null
                        info { "Set serial api to null because rxChar ($rxCharacteristic) or txChar($txCharacteristic) not found" }
                        return@collect
                    }
                    delegateSerialApi = unsafeApiImplFactory(
                        rxCharacteristic = rxCharacteristic,
                        txCharacteristic = txCharacteristic,
                        scope = scope
                    )
                    lock.notifyAll()
                }
            }
        }
    }


    override suspend fun getReceiveBytesFlow() = receiveByteFlow.asSharedFlow()

    override suspend fun sendBytes(data: ByteArray) {
        var serialApi = delegateSerialApi
        while (serialApi == null) {
            warn { "Try send byte to null delegate serial api" }
            lock.wait()
            serialApi = delegateSerialApi
        }
        serialApi.sendBytes(data)
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