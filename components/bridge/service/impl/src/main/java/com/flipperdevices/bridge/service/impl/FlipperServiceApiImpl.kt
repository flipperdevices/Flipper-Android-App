package com.flipperdevices.bridge.service.impl

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.impl.manager.FlipperBleManagerImpl
import com.flipperdevices.bridge.service.api.FlipperServiceApi
import com.flipperdevices.bridge.service.api.provider.FlipperBleServiceError
import com.flipperdevices.bridge.service.impl.delegate.FlipperServiceConnectDelegate
import com.flipperdevices.bridge.service.impl.provider.error.FlipperServiceErrorListener
import com.flipperdevices.core.utils.preference.FlipperSharedPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import timber.log.Timber

class FlipperServiceApiImpl(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val serviceErrorListener: FlipperServiceErrorListener
) : FlipperServiceApi {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val scope = lifecycleOwner.lifecycleScope
    private val bleManager: FlipperBleManager = FlipperBleManagerImpl(context, scope)
    private val connectDelegate = FlipperServiceConnectDelegate(bleManager, context)

    fun internalInit() {
        connectToDeviceOnStartup()
    }

    override fun getRequestApi(): FlipperRequestApi {
        return bleManager.flipperRequestApi
    }

    override suspend fun reconnect(deviceId: String) {
        connectDelegate.reconnect(deviceId)
    }

    override suspend fun reconnect(device: BluetoothDevice) {
        connectDelegate.reconnect()
    }

    private fun connectToDeviceOnStartup() = scope.launch {
        val deviceId = sharedPreferences.getString(FlipperSharedPreferencesKey.DEVICE_ID, null)
        if (deviceId == null) {
            Timber.e("Flipper id not found in storage")
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_DEVICE_NOT_STORED)
            return@launch
        }

        try {
            reconnect(deviceId)
        } catch (securityException: SecurityException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_PERMISSION)
            Timber.e(securityException, "On initial connect to device")
        } catch (bleDisabled: BluetoothDisabledException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_BLUETOOTH_DISABLED)
            Timber.e(bleDisabled, "On initial connect to device")
        } catch (timeout: TimeoutCancellationException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_TIMEOUT)
            Timber.e(timeout, "On initial connect to device")
        } catch (illegalArgumentException: IllegalArgumentException) {
            serviceErrorListener.onError(FlipperBleServiceError.CONNECT_REQUIRE_REBOUND)
            Timber.e(illegalArgumentException, "On initial connect to device")
        }
    }
}
