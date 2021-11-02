package com.flipperdevices.bridge.service.impl

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.impl.manager.FlipperBleManagerImpl
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceError
import com.flipperdevices.bridge.service.impl.delegate.FlipperServiceConnectDelegate
import com.flipperdevices.bridge.service.impl.di.FlipperServiceComponent
import com.flipperdevices.bridge.service.impl.provider.error.FlipperServiceErrorListener
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.preference.FlipperSharedPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.exception.BluetoothDisabledException

class FlipperServiceApiImpl(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val serviceErrorListener: FlipperServiceErrorListener
) : FlipperServiceApi, LogTagProvider {
    override val TAG = "FlipperServiceApi"

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val scope = lifecycleOwner.lifecycleScope
    private val bleManager: FlipperBleManager = FlipperBleManagerImpl(context, scope)
    private val connectDelegate = FlipperServiceConnectDelegate(bleManager, context)

    override val connectionInformationApi = bleManager.connectionInformationApi
    override val requestApi = bleManager.flipperRequestApi
    override val flipperInformationApi = bleManager.informationApi

    init {
        ComponentHolder.component<FlipperServiceComponent>().inject(this)
    }

    fun internalInit() {
        info { "Internal init and try connect" }
        connectToDeviceOnStartup()
    }

    override suspend fun reconnect(deviceId: String) {
        info { "Reconnect to device $deviceId" }
        connectDelegate.reconnect(deviceId)
    }

    override suspend fun reconnect(device: BluetoothDevice) {
        info { "Reconnect to device ${device.address}" }
        connectDelegate.reconnect(device)
    }

    suspend fun close() {
        info { "Close manager and disconnect" }
        connectDelegate.disconnect()
        info { "Disconnect successful, close manager" }
        bleManager.close()
    }

    private fun connectToDeviceOnStartup() = scope.launch {
        val deviceId = sharedPreferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null)
        if (deviceId == null) {
            error { "Flipper id not found in storage" }
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_DEVICE_NOT_STORED)
            return@launch
        }

        try {
            reconnect(deviceId)
        } catch (securityException: SecurityException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION)
            error(securityException) { "On initial connect to device" }
        } catch (bleDisabled: BluetoothDisabledException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED)
            error(bleDisabled) { "On initial connect to device" }
        } catch (timeout: TimeoutCancellationException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_TIMEOUT)
            error(timeout) { "On initial connect to device" }
        } catch (illegalArgumentException: IllegalArgumentException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_REQUIRE_REBOUND)
            error(illegalArgumentException) { "On initial connect to device" }
        }
    }
}
