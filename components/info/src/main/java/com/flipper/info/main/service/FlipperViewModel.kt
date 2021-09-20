package com.flipper.info.main.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.api.FlipperApi
import com.flipper.bridge.api.device.FlipperDeviceApi
import com.flipper.bridge.impl.manager.FlipperBleManager
import com.flipper.bridge.model.FlipperGATTInformation
import com.flipper.core.utils.toast
import com.flipper.info.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.exception.BluetoothDisabledException
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import timber.log.Timber

class FlipperViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private var currentDevice: FlipperDeviceApi? = null
    private val echoAnswers = MutableStateFlow(emptyList<ByteArray>())
    private val deviceInformation = MutableStateFlow(FlipperGATTInformation())
    private val allEchoAnswers = mutableListOf<ByteArray>()
    private val connectionState = MutableStateFlow<ConnectionState?>(null)

    fun getEchoAnswers(): StateFlow<List<ByteArray>> {
        return echoAnswers
    }

    fun sendEcho(text: String) {
        currentDevice?.getBleManager()?.sendEcho(text)
    }

    fun getDeviceInformation(): StateFlow<FlipperGATTInformation> {
        return deviceInformation
    }

    fun getConnectionState(): StateFlow<ConnectionState?> = connectionState

    fun connectAndStart(deviceId: String) = viewModelScope.launch {
        currentDevice = FlipperApi.flipperPairApi.getFlipperApi(context, deviceId)
        val bleManager = currentDevice!!.getBleManager()
        async { subscribeToEcho(bleManager) }
        async { subscribeToInformationState(bleManager) }
        async { subscribeToConnectionState(bleManager) }
        try {
            FlipperApi.flipperPairApi.connect(context, currentDevice!!)
        } catch (securityException: SecurityException) {
            context.toast(R.string.info_pair_err_permission)
            Timber.e(securityException)
        } catch (bleDisabled: BluetoothDisabledException) {
            context.toast(R.string.info_pair_err_ble_disabled)
            Timber.e(bleDisabled)
        } catch (timeout: TimeoutCancellationException) {
            context.toast(R.string.info_pair_err_timeout)
            Timber.e(timeout)
        } catch (illegalArgumentException: IllegalArgumentException) {
            context.toast(R.string.info_pair_err_not_bounded)
            Timber.e(illegalArgumentException)
        }
    }

    private suspend fun subscribeToEcho(bleManager: FlipperBleManager) =
        withContext(Dispatchers.IO) {
            bleManager.getEchoState().collect {
                if (it.isEmpty()) {
                    return@collect
                }
                allEchoAnswers.add(it)
                echoAnswers.emit(ArrayList(allEchoAnswers))
            }
        }

    private suspend fun subscribeToInformationState(bleManager: FlipperBleManager) =
        withContext(Dispatchers.IO) {
            bleManager.getInformationState().collect {
                deviceInformation.emit(it)
            }
        }

    private suspend fun subscribeToConnectionState(bleManager: FlipperBleManager) =
        withContext(Dispatchers.IO) {
            bleManager.stateAsFlow().collect {
                connectionState.emit(it)
            }
        }

    override fun onCleared() {
        super.onCleared()
        if (currentDevice?.getBleManager()?.isConnected == true) {
            currentDevice?.getBleManager()?.disconnect()
        }
    }
}
