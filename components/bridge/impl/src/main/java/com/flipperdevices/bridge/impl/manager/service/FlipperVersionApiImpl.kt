package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.model.FlipperVersionInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class FlipperVersionApiImpl(
    private val settingsStore: DataStore<Settings>
) : FlipperVersionApi, BluetoothGattServiceWrapper, LogTagProvider {
    override val TAG = "FlipperVersionApi"

    private val flipperVersionState = MutableStateFlow(FlipperVersionInformation())
    private var apiVersionCharacteristics: BluetoothGattCharacteristic? = null

    override fun getFlipperVersion(): StateFlow<FlipperVersionInformation> = flipperVersionState

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        getServiceOrLog(gatt, Constants.BLEInformationService.SERVICE_UUID)?.let { service ->
            apiVersionCharacteristics = service.getCharacteristic(
                Constants.BLEInformationService.API_VERSION
            )
        }
        val serviceFounded = apiVersionCharacteristics != null
        if (!serviceFounded) {
            flipperVersionState.update { FlipperVersionInformation(isSupported = false) }
        }
        return serviceFounded
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        val ignoreUnsupported = settingsStore.data.first().ignoreUnsupportedVersion

        bleManager.readCharacteristicUnsafe(apiVersionCharacteristics)
            .with { _, data ->
                info { "Found information about version $data" }
                val content = data.value ?: return@with
                val apiVersion = String(content)
                    .replace("[^\\x20-\\x7E]", "")
                    .trim()
                onSupportedVersionReceived(bleManager, apiVersion, ignoreUnsupported)
            }.enqueue()
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        flipperVersionState.emit(FlipperVersionInformation())
    }

    private fun onSupportedVersionReceived(
        bleManager: UnsafeBleManager,
        apiVersion: String,
        ignoreUnsupported: Boolean
    ) {
        info { "Api version is $apiVersion" }
        val apiVersionNumber = apiVersion.toFloatOrNull()
        info { "Parsed api version number is $apiVersionNumber" }
        var deviceSupportedStatus = apiVersionNumber != null &&
            apiVersionNumber >= Constants.API_SUPPORTED_VERSION
        if (ignoreUnsupported) {
            deviceSupportedStatus = true
        }
        bleManager.setDeviceSupportedStatus(deviceSupportedStatus)
        flipperVersionState.update {
            it.copy(
                version = apiVersion,
                isSupported = deviceSupportedStatus
            )
        }
    }
}
