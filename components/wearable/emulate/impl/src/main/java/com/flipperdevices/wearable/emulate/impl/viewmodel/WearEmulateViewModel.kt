package com.flipperdevices.wearable.emulate.impl.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.wear.widget.ConfirmationOverlay
import com.flipperdevices.core.activityholder.CurrentActivityHolder
import com.flipperdevices.core.ktx.android.toast
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.wearable.emulate.impl.R
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.nio.charset.Charset
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

private const val CAPABILITY_PHONE_APP = "emulate_remote_flipper_phone_app"
private const val MESSAGE_PATH_EMULATE = "/emulate"
private const val MESSAGE_PATH_EMULATE_CLOSE = "/emulate_close"
private const val MESSAGE_PATH_EMULATE_RESULT = "/emulate_result"

class WearEmulateViewModel(
    application: Application
) : AndroidViewModel(application), LogTagProvider, MessageClient.OnMessageReceivedListener {
    override val TAG = "WearEmulateViewModel"

    private val keyPath = "path/to/key"
    private val wearEmulateStateFlow = MutableStateFlow<WearEmulateState>(WearEmulateState.Loading)
    private val capabilityClient by lazy { Wearable.getCapabilityClient(application) }
    private val messageClient by lazy { Wearable.getMessageClient(application) }

    init {
        messageClient.addListener(this)
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

            val foundedNode = capabilityInfo.nodes.firstOrNull { it.isNearby }
                ?: capabilityInfo.nodes.firstOrNull()
            val foundedNodeId = foundedNode?.id
            if (foundedNodeId == null) {
                wearEmulateStateFlow.emit(WearEmulateState.NotFoundNode)
            } else wearEmulateStateFlow.emit(WearEmulateState.FoundNode(foundedNodeId))
        } catch (ignored: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            error(throwable) { "Capability request failed to return any results." }
        }
    }

    fun onClickEmulate() {
        val wearEmulateState = wearEmulateStateFlow.value
        if (wearEmulateState !is WearEmulateState.FoundNode) {
            error {
                "Call #onClickEmulate not on FoundNode state. Current state is $wearEmulateState"
            }
            return
        }
        wearEmulateStateFlow.update { WearEmulateState.Emulating(wearEmulateState.nodeId) }
        viewModelScope.launch {
            try {
                messageClient.sendMessage(
                    wearEmulateState.nodeId,
                    MESSAGE_PATH_EMULATE,
                    keyPath.toByteArray(Charset.forName("UTF-8"))
                ).await()
            } catch (throwable: Exception) {
                error(throwable) { "Error when sending a request for execution" }
                CurrentActivityHolder.getCurrentActivity()?.let {
                    it.toast(R.string.keyscreen_emulating_error)
                    ConfirmationOverlay()
                        .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                        .showOn(it)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        val nodeId = (wearEmulateStateFlow.value as? WearEmulateState.FoundNode)?.nodeId
            ?: (wearEmulateStateFlow.value as? WearEmulateState.Emulating)?.nodeId
        if (nodeId != null) {
            try {
                runBlocking {
                    messageClient.sendMessage(
                        nodeId,
                        MESSAGE_PATH_EMULATE_CLOSE,
                        keyPath.toByteArray(Charset.forName("UTF-8"))
                    ).await()
                }
            } catch (throwable: Throwable) {
                error(throwable) { "Error when sending a request for stop" }
            }
        }
    }

    override fun onMessageReceived(message: MessageEvent) {
        if (message.path == MESSAGE_PATH_EMULATE_RESULT) {
            wearEmulateStateFlow.update {
                if (it is WearEmulateState.Emulating) {
                    WearEmulateState.FoundNode(it.nodeId)
                } else it
            }
        }
    }
}
