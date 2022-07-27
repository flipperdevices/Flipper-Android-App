package com.flipperdevices.bridge.impl.manager.service

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.manager.ktx.state.FlipperSupportedState
import com.flipperdevices.bridge.api.manager.service.FlipperVersionApi
import com.flipperdevices.bridge.api.model.FlipperVersionInformation
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.impl.manager.UnsafeBleManager
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class FlipperVersionApiImpl(
    private val settingsStore: DataStore<Settings>
) : BluetoothGattServiceWrapper, FlipperVersionApi, LogTagProvider {
    override val TAG = "FlipperVersionApi"

    private val flipperVersionInformationStateFlow =
        MutableStateFlow<FlipperVersionInformation?>(null)

    private var apiVersionCharacteristics: BluetoothGattCharacteristic? = null

    override fun getVersionInformationFlow(): StateFlow<FlipperVersionInformation?> =
        flipperVersionInformationStateFlow

    override fun onServiceReceived(gatt: BluetoothGatt): Boolean {
        getServiceOrLog(gatt, Constants.BLEInformationService.SERVICE_UUID)?.let { service ->
            apiVersionCharacteristics = service.getCharacteristic(
                Constants.BLEInformationService.API_VERSION
            )
        }
        val serviceFounded = apiVersionCharacteristics != null
        if (!serviceFounded) {
            error { "Support version not founded" }
        }
        return serviceFounded
    }

    override suspend fun initialize(bleManager: UnsafeBleManager) {
        val ignoreUnsupported = settingsStore.data.first().ignoreUnsupportedVersion
        if (ignoreUnsupported) {
            bleManager.setDeviceSupportedStatus(FlipperSupportedState.READY)
            return
        }

        bleManager.readCharacteristicUnsafe(apiVersionCharacteristics)
            .with { _, data ->
                info { "Found information about version $data" }
                val content = data.value ?: return@with
                try {
                    val apiVersion = String(content)
                    onSupportedVersionReceived(bleManager, apiVersion)
                } catch (e: Exception) {
                    error(e) { "Failed parse api version $content" }

                    bleManager.setDeviceSupportedStatus(
                        FlipperSupportedState.DEPRECATED_FLIPPER
                    )
                }
            }.enqueue()
    }

    override suspend fun reset(bleManager: UnsafeBleManager) {
        // Do nothing
    }

    private fun onSupportedVersionReceived(bleManager: UnsafeBleManager, apiVersion: String) {
        info { "Api version is $apiVersion" }
        val filteredApiVersion = apiVersion.replace("[^0-9.]", "")
        info { "Filtered api version is $filteredApiVersion" }
        val parts = apiVersion.trim().split(".")
        val majorPart = parts.firstOrNull()
        val minorPart = if (parts.size >= 2) parts[1] else null
        info { "Founded ${parts.size} parts. Major part is $majorPart, minor is $minorPart" }
        val versionInformation = FlipperVersionInformation(
            majorVersion = majorPart?.toIntOrNull() ?: 0,
            minorVersion = minorPart?.toIntOrNull() ?: 0
        )
        flipperVersionInformationStateFlow.update { versionInformation }
        bleManager.setDeviceSupportedStatus(versionInformation.toSupportedState())
    }
}

private fun FlipperVersionInformation.toSupportedState() = when {
    this < Constants.API_SUPPORTED_VERSION -> FlipperSupportedState.DEPRECATED_FLIPPER
    this >= Constants.API_MAX_SUPPORTED_VERSION -> FlipperSupportedState.DEPRECATED_APPLICATION
    else -> FlipperSupportedState.READY
}
