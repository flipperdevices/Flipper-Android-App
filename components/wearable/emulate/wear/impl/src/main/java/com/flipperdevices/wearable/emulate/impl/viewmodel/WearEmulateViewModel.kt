package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.wearable.emulate.impl.api.EMULATE_PATH_KEY
import com.flipperdevices.wearable.emulate.impl.di.DaggerWearEmulateComponent
import com.flipperdevices.wearable.emulate.impl.model.KeyToEmulate
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class WearEmulateViewModel @VMInject constructor(
    @TangleParam(EMULATE_PATH_KEY)
    private val keyPath: String
) : LifecycleViewModel(),
    LogTagProvider {
    override val TAG = "WearEmulateViewModel"

    private val wearableComponent by lazy {
        DaggerWearEmulateComponent.factory()
            .create(
                ComponentHolder.component(),
                lifecycleOwner = this,
                keyToEmulate = KeyToEmulate(keyPath)
            )
    }

    init {
        combine(
            wearableComponent.connectionChannelHelper.getState(),
            wearableComponent.connectionTester.getState(),
            wearableComponent.flipperStatusListener.getState(),
            wearableComponent.emulateStateListener.getState()
        ) { channelState, connectionTester, flipperConnectStatus, emulateState ->
            wearableComponent.wearEmulateStateMachine.onStatesUpdate(
                channelState,
                connectionTester,
                flipperConnectStatus,
                emulateState
            )
        }.launchIn(viewModelScope)
    }

    fun getWearEmulateState(): StateFlow<WearEmulateState> =
        wearableComponent.wearEmulateStateMachine.getStateFlow()

    fun onClickEmulate() = wearableComponent.emulateHelper.onStartEmulate()

    fun onShortEmulate() = wearableComponent.emulateHelper.onSend()

    fun onStopEmulate() = wearableComponent.emulateHelper.onStopEmulate()

    override fun onCleared() {
        super.onCleared()
        wearableComponent.connectionChannelHelper.onClear()
    }
}
