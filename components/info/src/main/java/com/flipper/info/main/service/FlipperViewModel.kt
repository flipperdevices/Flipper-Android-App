package com.flipper.info.main.service

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipper.bridge.api.FlipperApi
import com.flipper.bridge.api.device.FlipperDeviceApi
import com.flipper.bridge.model.FlipperGATTInformation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FlipperViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application
    private var currentDevice: FlipperDeviceApi? = null
    private val echoAnswers = MutableStateFlow(emptyList<ByteArray>())
    private val deviceInformation = MutableStateFlow(FlipperGATTInformation())
    private val allEchoAnswers = mutableListOf<ByteArray>()

    fun getEchoAnswers(): StateFlow<List<ByteArray>> {
        return echoAnswers
    }

    fun sendEcho(text: String) {
        currentDevice?.getBleManager()?.sendEcho(text)
    }

    fun getDeviceInformation(): StateFlow<FlipperGATTInformation> {
        return deviceInformation
    }

    fun connectAndStart(deviceId: String) = viewModelScope.launch {
        currentDevice = FlipperApi.flipperPairApi.connect(context, deviceId)
        val bleManager = currentDevice!!.getBleManager()
        bleManager.getEchoState().collect {
            if (it.isEmpty()) {
                return@collect
            }
            allEchoAnswers.add(it)
            echoAnswers.emit(ArrayList(allEchoAnswers))
        }
        bleManager.getInformationState().collect { deviceInformation.emit(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (currentDevice?.getBleManager()?.isConnected == true) {
            currentDevice?.getBleManager()?.disconnect()
        }
    }
}
