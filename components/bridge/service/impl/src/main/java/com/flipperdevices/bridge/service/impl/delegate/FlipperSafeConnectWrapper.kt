package com.flipperdevices.bridge.service.impl.delegate

import androidx.datastore.core.DataStore
import com.flipperdevices.bridge.api.error.FlipperBleServiceError
import com.flipperdevices.bridge.api.error.FlipperServiceErrorListener
import com.flipperdevices.bridge.service.impl.model.DeviceChangedMacException
import com.flipperdevices.bridge.service.impl.model.SavedFlipperConnectionInfo
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ktx.jre.FlipperDispatchers
import com.flipperdevices.core.ktx.jre.launchWithLock
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.pb.PairSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import javax.inject.Inject
import javax.inject.Provider

class FlipperSafeConnectWrapper @Inject constructor(
    scopeProvider: Provider<CoroutineScope>,
    serviceErrorListenerProvider: Provider<FlipperServiceErrorListener>,
    connectDelegateProvider: Provider<FlipperServiceConnectDelegate>,
    dataStoreProvider: Provider<DataStore<PairSettings>>
) : LogTagProvider {
    override val TAG = "FlipperSafeConnectWrapper"

    private val isConnectingMutableStateFlow = MutableStateFlow(false)

    // It makes sure that we don't change the currentConnectingJob variable in different threads
    private val mutex = Mutex()
    private var currentConnectingJob: Job? = null

    private val scope by scopeProvider
    private val serviceErrorListener by serviceErrorListenerProvider
    private val connectDelegate by connectDelegateProvider
    private val dataStore by dataStoreProvider

    fun isConnectingFlow() = isConnectingMutableStateFlow.asStateFlow()

    suspend fun onActiveDeviceUpdate(
        connectionInfo: SavedFlipperConnectionInfo?,
        force: Boolean
    ) {
        launchWithLock(mutex, scope, "onActiveDeviceUpdate") {
            if (force.not() && currentConnectingJob?.isActive == true) {
                info { "onActiveDeviceUpdate called without force, so skip reinvalidate job" }
                return@launchWithLock
            }
            info { "Call cancel and join to current job" }
            currentConnectingJob?.cancelAndJoin()
            info { "Job canceled! Call connect again" }
            currentConnectingJob = scope.launch(FlipperDispatchers.workStealingDispatcher) {
                var jobCompleted = false
                isConnectingMutableStateFlow.emit(true)
                do {
                    val deviceUpdateResult = runCatching {
                        onActiveDeviceUpdateInternal(connectionInfo)
                    }
                    val errorOnDeviceUpdate = deviceUpdateResult.exceptionOrNull()
                    if (errorOnDeviceUpdate != null) {
                        error(errorOnDeviceUpdate) { "Unexpected error on activeDeviceUpdate" }
                    }
                    if (deviceUpdateResult.getOrNull() == true) {
                        jobCompleted = true
                    }
                } while (isActive && jobCompleted.not())
                if (jobCompleted) {
                    isConnectingMutableStateFlow.emit(false)
                }
            }
        }
    }

    private suspend fun onActiveDeviceUpdateInternal(
        connectionInfo: SavedFlipperConnectionInfo?
    ): Boolean {
        if (connectionInfo == null || connectionInfo.id.isBlank()) {
            error { "Flipper id not found in storage" }
            connectDelegate.disconnect()
            return true
        }

        try {
            return connectDelegate.reconnect(connectionInfo)
        } catch (securityException: SecurityException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION)
            error(securityException) { "On initial connect to device" }

            return true
        } catch (bleDisabled: BluetoothDisabledException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED)
            error(bleDisabled) { "On initial connect to device" }

            return true
        } catch (changedMac: DeviceChangedMacException) {
            error(changedMac) { "Mac changed from ${changedMac.oldMacAddress} to ${changedMac.newMacAddress}" }
            dataStore.updateData { data ->
                if (data.deviceId == changedMac.oldMacAddress) {
                    return@updateData data.toBuilder()
                        .setDeviceId(changedMac.newMacAddress)
                        .build()
                } else {
                    error { "Wrong device id for mac address change request" }
                    return@updateData data
                }
            }

            return false
        }
    }
}
