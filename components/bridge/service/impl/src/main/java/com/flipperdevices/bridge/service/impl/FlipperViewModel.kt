package com.flipperdevices.bridge.service.impl

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.api.manager.FlipperBleManager
import com.flipperdevices.bridge.api.manager.FlipperRequestApi
import com.flipperdevices.bridge.api.model.FlipperGATTInformation
import com.flipperdevices.bridge.provider.FlipperApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.nordicsemi.android.ble.ktx.state.ConnectionState

class FlipperViewModel(
    application: Application,
    deviceId: String
) : AndroidViewModel(application) {
    private val context = application
    private var currentDevice = FlipperApi.flipperPairApi.getFlipperApi(context, deviceId)
    private val deviceInformation = MutableStateFlow(FlipperGATTInformation())
    private val connectionState = MutableStateFlow<ConnectionState?>(null)

    fun getDeviceInformation(): StateFlow<FlipperGATTInformation> {
        return deviceInformation
    }

    fun getRequestApi(): FlipperRequestApi {
        return currentDevice.getBleManager().flipperRequestApi
    }

    fun getConnectionState(): StateFlow<ConnectionState?> = connectionState

    fun connectAndStart() = viewModelScope.launch {
        val bleManager = currentDevice.getBleManager()
        async { subscribeToInformationState(bleManager) }
        async { subscribeToConnectionState(bleManager) }
    }

    private suspend fun subscribeToInformationState(bleManager: FlipperBleManager) =
        withContext(Dispatchers.IO) {
            bleManager.getInformationStateFlow().collect {
                deviceInformation.emit(it)
            }
        }

    private suspend fun subscribeToConnectionState(bleManager: FlipperBleManager) =
        withContext(Dispatchers.IO) {
            bleManager.getConnectionStateFlow().collect {
                connectionState.emit(it)
            }
        }

    override fun onCleared() {
        super.onCleared()
        if (currentDevice?.getBleManager()?.isDeviceConnected == true) {
            currentDevice?.getBleManager()?.disconnectDevice()
        }
    }
}
