package com.flipperdevices.bridge.service.impl.delegate

import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import javax.inject.Inject
import javax.inject.Provider

class FlipperSafeConnectWrapper @Inject constructor(
    scopeProvider: Provider<CoroutineScope>,
    serviceErrorListenerProvider: Provider<FlipperServiceErrorListener>,
    connectDelegateProvider: Provider<FlipperServiceConnectDelegate>
) : LogTagProvider {
    override val TAG = "FlipperSafeConnectWrapper"

    // It makes sure that we don't change the currentConnectingJob variable in different threads
    private val mutex = Mutex()
    private var currentConnectingJob: Job? = null

    private val scope by scopeProvider
    private val serviceErrorListener by serviceErrorListenerProvider
    private val connectDelegate by connectDelegateProvider

    suspend fun onActiveDeviceUpdate(
        deviceId: String?
    ) = launchWithLock(mutex, scope, "onActiveDeviceUpdate") {
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

    fun isTryingConnected() = currentConnectingJob?.isActive ?: false

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
