package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.error
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.wearable.emulate.impl.api.EMULATE_PATH_KEY
import com.flipperdevices.wearable.emulate.impl.di.DaggerWearEmulateComponent
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import com.google.android.gms.wearable.CapabilityClient
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

private const val CAPABILITY_PHONE_APP = "emulate_remote_flipper_phone_app"

class WearEmulateViewModel @VMInject constructor(
    private val capabilityClient: CapabilityClient,
    @TangleParam(EMULATE_PATH_KEY)
    private val keyPath: String
) : LifecycleViewModel(),
    LogTagProvider {
    override val TAG = "WearEmulateViewModel"

    private val wearableComponent by lazy {
        DaggerWearEmulateComponent.factory()
            .create(ComponentHolder.component(), lifecycleOwner = this)
    }

    private val keyType by lazy { FlipperKeyType.getByExtension(File(keyPath).extension) }
    private val wearEmulateStateFlow =
        MutableStateFlow<WearEmulateState>(WearEmulateState.Loading(keyType))

    init {
        viewModelScope.launch {
            checkIfPhoneHasApp()
        }
    }

    fun getWearEmulateState(): StateFlow<WearEmulateState> = wearEmulateStateFlow

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
                info { "Can't found node $foundedNode" }
                wearEmulateStateFlow.emit(WearEmulateState.NotFoundNode(keyType))
            } else {
                info { "Found node $foundedNode" }
                wearEmulateStateFlow.emit(WearEmulateState.FoundNode(keyType, foundedNodeId))
            }
        } catch (ignored: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            error(throwable) { "Capability request failed to return any results." }
        }
    }

    fun onClickEmulate() {
        info { "Start #onClickEmulate" }
        val wearEmulateState = wearEmulateStateFlow.value
        if (wearEmulateState !is WearEmulateState.FoundNode) {
            error {
                "Call #onClickEmulate not on FoundNode state. Current state is $wearEmulateState"
            }
            return
        }
        wearEmulateStateFlow.update { WearEmulateState.Emulating(keyType, wearEmulateState.nodeId) }
        //sendCommand(wearEmulateState.nodeId, MESSAGE_PATH_EMULATE)
    }

    fun onShortEmulate() {
        info { "Start #onShortEmulate" }
        val wearEmulateState = wearEmulateStateFlow.value
        if (wearEmulateState !is WearEmulateState.FoundNode) {
            error {
                "Call #onClickEmulate not on FoundNode state. Current state is $wearEmulateState"
            }
            return
        }
        wearEmulateStateFlow.update { WearEmulateState.Emulating(keyType, wearEmulateState.nodeId) }
        //sendCommand(wearEmulateState.nodeId, MESSAGE_PATH_EMULATE_SHORT)
    }

    fun onStopEmulate() {
        info { "Start #onStopEmulate" }
        val wearEmulateState = wearEmulateStateFlow.value
        if (wearEmulateState !is WearEmulateState.Emulating) {
            error {
                "Call #onStopEmulate not on Emulating state. Current state is $wearEmulateState"
            }
            return
        }
        //sendCommand(wearEmulateState.nodeId, MESSAGE_PATH_EMULATE_CLOSE)
    }

    override fun onCleared() {
        super.onCleared()

    }
}
