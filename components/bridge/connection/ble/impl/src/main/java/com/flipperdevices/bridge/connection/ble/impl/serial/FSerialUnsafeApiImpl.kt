package com.flipperdevices.bridge.connection.ble.impl.serial

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.flipperdevices.bridge.connection.ble.api.FBleDeviceSerialConfig
import com.flipperdevices.bridge.connection.ble.impl.model.BLEConnectionPermissionException
import com.flipperdevices.bridge.connection.common.api.FSerialDeviceApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID
import javax.inject.Named
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import no.nordicsemi.android.common.core.DataByteArray
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattCharacteristic
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattService
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices

private const val DAGGER_ID_CHARACTERISTIC_RX = "rx_service"
private const val DAGGER_ID_CHARACTERISTIC_TX = "tx_service"

class FSerialUnsafeApiImpl @AssistedInject constructor(
    @Assisted(DAGGER_ID_CHARACTERISTIC_RX) val rxCharacteristic: ClientBleGattCharacteristic,
    @Assisted(DAGGER_ID_CHARACTERISTIC_TX) val txCharacteristic: ClientBleGattCharacteristic,
    @Assisted scope: CoroutineScope,
    private val context: Context
) : FSerialDeviceApi {
    private val receiverByteFlow = MutableSharedFlow<ByteArray>()

    init {
        scope.launch {
            rxCharacteristic.getNotifications(
                bufferOverflow = BufferOverflow.SUSPEND
            ).collect {
                receiverByteFlow.emit(it.value)
            }
        }
    }

    override suspend fun getReceiveBytesFlow() = receiverByteFlow.asSharedFlow()

    override suspend fun sendBytes(data: ByteArray) {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw BLEConnectionPermissionException()
        }
        txCharacteristic.splitWrite(DataByteArray(data))
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            @Assisted(DAGGER_ID_CHARACTERISTIC_RX) rxCharacteristic: ClientBleGattCharacteristic,
            @Assisted(DAGGER_ID_CHARACTERISTIC_TX) txCharacteristic: ClientBleGattCharacteristic,
            scope: CoroutineScope
        ): FSerialUnsafeApiImpl
    }
}