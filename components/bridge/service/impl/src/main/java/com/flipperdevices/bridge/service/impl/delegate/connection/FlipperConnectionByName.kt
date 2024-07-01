package com.flipperdevices.bridge.service.impl.delegate.connection

import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.scanner.FlipperScanner
import com.flipperdevices.bridge.api.utils.Constants
import com.flipperdevices.bridge.service.impl.model.DeviceChangedMacException
import com.flipperdevices.bridge.service.impl.model.SavedFlipperConnectionInfo
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Provider

/**
 * It's a fallback if the user's MAC address has changed, but the flipper is the same.
 * This can happen in two cases:
 * - Changing official firmware to custom firmware (and back again)
 * - Updating official firmware after PR https://github.com/flipperdevices/flipperzero-firmware/pull/3723
 */
class FlipperConnectionByName @Inject constructor(
    bleManagerProvider: Provider<FlipperBleManager>,
    scannerProvider: Provider<FlipperScanner>
) : FlipperConnectionDelegate, LogTagProvider {
    override val TAG = "FlipperConnectionByName"

    private val bleManager by bleManagerProvider
    private val scanner by scannerProvider

    @OptIn(FlowPreview::class)
    override suspend fun connect(connectionInfo: SavedFlipperConnectionInfo): Boolean {
        info { "Start connection by $connectionInfo" }
        if (connectionInfo.name == null) {
            error { "Failed connect by ID and flipper name is unknown" }
            return false
        }

        val devices = scanner.findFlipperByName(connectionInfo.name).filter {
            it.address != connectionInfo.id
        }.timeout(Constants.BLE.CONNECT_TIME)
            .catch { exception ->
                if (exception !is TimeoutCancellationException) {
                    // Throw other exceptions.
                    throw exception
                }
            }.toList()
        info { "Found: $devices" }

        for (device in devices) {
            info { "Connect to ${device.address}..." }
            val result = runCatching {
                withTimeout(Constants.BLE.NEW_CONNECT_TIME) {
                    bleManager.connectToDevice(device.device)
                }
            }
            if (result.isSuccess) {
                info { "Connect to ${device.address} SUCCESS" }

                throw DeviceChangedMacException(
                    oldMacAddress = connectionInfo.id,
                    newMacAddress = device.address
                )
            } else {
                error(result.exceptionOrNull()) { "Connect to ${device.address} FAILED" }
            }
        }

        return false
    }
}
