package com.flipperdevices.wearable.setup.impl.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.widget.ConfirmationOverlay
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.setup.impl.R
import com.flipperdevices.wearable.setup.impl.model.FindPhoneState
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val CAPABILITY_PHONE_APP = "verify_remote_flipper_phone_app"
private const val ANDROID_MARKET_APP_URI = "market://details?id=com.flipperdevices.app"

class FindPhoneViewModel(
    application: Application
) : AndroidViewModel(application), CapabilityClient.OnCapabilityChangedListener, LogTagProvider {
    override val TAG = "FindPhoneViewModel"

    private val findPhoneStateFlow = MutableStateFlow<FindPhoneState>(FindPhoneState.Loading)
    private val capabilityClient by lazy { Wearable.getCapabilityClient(application) }
    private val remoteActivityHelper by lazy { RemoteActivityHelper(application) }

    init {
        capabilityClient.addListener(this, CAPABILITY_PHONE_APP)
        checkPhone()
    }

    fun getFindPhoneModelFlow(): StateFlow<FindPhoneState> = findPhoneStateFlow

    fun openStore() {
        info { "#openAppInStoreOnPhone" }
        val intent = when (PhoneTypeHelper.getPhoneDeviceType(getApplication())) {
            PhoneTypeHelper.DEVICE_TYPE_ANDROID -> {
                info { "DEVICE_TYPE_ANDROID" }
                // Create Remote Intent to open Play Store listing of app on remote device.
                Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(ANDROID_MARKET_APP_URI))
            }
            else -> {
                info { "DEVICE_TYPE_ERROR_UNKNOWN" }
                return
            }
        }

        viewModelScope.launch {
            try {
                remoteActivityHelper.startRemoteActivity(intent).await()

                CurrentActivityHolder.getCurrentActivity()?.let {
                    it.toast(R.string.install_app_done)
                    ConfirmationOverlay().showOn(it)
                }
            } catch (cancellationException: CancellationException) {
                // Request was cancelled normally
                throw cancellationException
            } catch (throwable: Throwable) {
                error(throwable) { "Error execute $intent" }
                CurrentActivityHolder.getCurrentActivity()?.let {
                    it.toast(R.string.install_app_fail)
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(it)
                }
            }
        }
    }

    fun checkPhone() {
        info { "#checkPhone" }
        viewModelScope.launch {
            checkIfPhoneHasApp()
        }
    }

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
            findPhoneStateFlow.update { FindPhoneState.NotFound }
        } else {
            findPhoneStateFlow.update { FindPhoneState.Founded }
        }
    }

    override fun onCleared() {
        super.onCleared()
        capabilityClient.removeListener(this, CAPABILITY_PHONE_APP)
    }
}
