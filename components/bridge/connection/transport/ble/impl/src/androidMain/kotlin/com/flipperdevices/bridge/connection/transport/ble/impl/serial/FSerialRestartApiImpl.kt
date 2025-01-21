package com.flipperdevices.bridge.connection.transport.ble.impl.serial

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.flipperdevices.bridge.connection.transport.ble.impl.model.BLEConnectionPermissionException
import com.flipperdevices.bridge.connection.transport.common.api.serial.FSerialRestartApi
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import no.nordicsemi.android.kotlin.ble.client.main.service.ClientBleGattServices
import no.nordicsemi.android.kotlin.ble.core.data.BleWriteType
import no.nordicsemi.android.kotlin.ble.core.data.util.DataByteArray
import java.util.UUID

class FSerialRestartApiImpl @AssistedInject constructor(
    @Assisted private val services: StateFlow<ClientBleGattServices?>,
    @Assisted("service") private val serialServiceUuid: UUID,
    @Assisted("characteristic") private val resetCharUUID: UUID,
    private val context: Context
) : FSerialRestartApi, LogTagProvider {
    override val TAG = "FSerialRestartApi"
    override suspend fun restartRpc() {
        info { "Request restart rpc" }
        val resetChar = services.filterNotNull().map {
            it.findService(serialServiceUuid)
        }.filterNotNull()
            .map { it.findCharacteristic(resetCharUUID) }
            .filterNotNull()
            .first()
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw BLEConnectionPermissionException()
        }
        resetChar.write(DataByteArray(byteArrayOf(0)), BleWriteType.DEFAULT)
    }

    override suspend fun sendTrashBytesAndBrokeSession() {
        if (context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            throw BLEConnectionPermissionException()
        }
        val randomBytes = byteArrayOf(-61, 91, 69, 107, -128, -69, -42, 107, 53, -102)
        services.filterNotNull().map { gattServices -> gattServices.services }
            .map { gattServiceList -> gattServiceList.flatMap { gattService -> gattService.characteristics } }
            .onEach { characteristics -> characteristics.onEach { characteristic -> characteristic.write(DataByteArray(randomBytes),BleWriteType.DEFAULT) } }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            services: StateFlow<ClientBleGattServices?>,
            @Assisted("service") serialServiceUuid: UUID,
            @Assisted("characteristic") resetCharUUID: UUID
        ): FSerialRestartApiImpl
    }
}
