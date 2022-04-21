package com.flipperdevices.bridge.service.impl.delegate

import android.content.Context
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.core.ktx.jre.runBlockingWithLog
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

class FlipperSafeConnectWrapper(
    context: Context,
    bleManager: FlipperBleManager,
    private val scope: CoroutineScope,
    private val serviceErrorListener: FlipperServiceErrorListener
) : LogTagProvider {
    override val TAG = "FlipperSafeConnectWrapper"

    // It makes sure that we don't change the currentConnectingJob variable in different threads
    private val dispatcher = Dispatchers.Default.limitedParallelism(1)
    private var currentConnectingJob: Job? = null

    private val connectDelegate = FlipperServiceConnectDelegate(bleManager, scope, context)

    suspend fun onActiveDeviceUpdate(deviceId: String?) {
        scope.launch(dispatcher) {
            runBlockingWithLog {
                info { "Call cancel and join to current job" }
                currentConnectingJob?.cancelAndJoin()
                info { "Job canceled! Call connect again" }
                currentConnectingJob = scope.launch(Dispatchers.Default) {
                    var errorOnDeviceUpdate: Throwable?
                    do {
                        errorOnDeviceUpdate = runCatching {
                            onActiveDeviceUpdateInternal(deviceId)
                        }.exceptionOrNull()
                        if (errorOnDeviceUpdate != null) {
                            error(errorOnDeviceUpdate) { "Unexpected error on activeDeviceUpdate" }
                        }
                    } while (isActive && errorOnDeviceUpdate != null)
                }
            }
        }.join()
    }

    private suspend fun onActiveDeviceUpdateInternal(deviceId: String?) {
        if (deviceId.isNullOrBlank()) {
            error { "Flipper id not found in storage" }
            connectDelegate.disconnect()
            return
        }

        try {
            connectDelegate.reconnect(deviceId)
        } catch (securityException: SecurityException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION)
            error(securityException) { "On initial connect to device" }
        } catch (bleDisabled: BluetoothDisabledException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED)
            error(bleDisabled) { "On initial connect to device" }
        }
    }
}
