package com.flipperdevices.wearable.setup.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.setup.impl.model.FindPhoneModel
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val CAPABILITY_PHONE_APP = "verify_remote_flipper_phone_app"

class FindPhoneViewModel(
    application: Application
) : AndroidViewModel(application), CapabilityClient.OnCapabilityChangedListener, LogTagProvider {
    override val TAG = "FindPhoneViewModel"

    private val findPhoneModelFlow = MutableStateFlow<FindPhoneModel>(FindPhoneModel.Loading)
    private val capabilityClient by lazy { Wearable.getCapabilityClient(application) }

    init {
        capabilityClient.addListener(this, CAPABILITY_PHONE_APP)
        viewModelScope.launch {
            checkIfPhoneHasApp()
        }
    }

    fun getFindPhoneModelFlow(): StateFlow<FindPhoneModel> = findPhoneModelFlow

    private suspend fun checkIfPhoneHasApp() {
        info { "#checkIfPhoneHasApp" }

        try {
            val capabilityInfo = capabilityClient
                .getCapability(CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)
                .await()

            info { "Capability request succeeded" }

            onCapabilityChanged(capabilityInfo)
        } catch (ignored: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            error(throwable) { "Capability request failed to return any results." }
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        val foundedDevice = capabilityInfo.nodes.firstOrNull()
        if (foundedDevice == null) {
            findPhoneModelFlow.update { FindPhoneModel.NotFound }
        } else {
            findPhoneModelFlow.update { FindPhoneModel.Founded(foundedDevice.displayName) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        capabilityClient.removeListener(this, CAPABILITY_PHONE_APP)
    }
}
