package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.viewModelScope
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ktx.jre.then
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.log.info
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.ConnectStatusOuterClass
import com.flipperdevices.wearable.emulate.impl.api.EMULATE_PATH_KEY
import com.flipperdevices.wearable.emulate.impl.di.DaggerWearEmulateComponent
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
            .create(ComponentHolder.component(), lifecycleOwner = this)
    }

    init {
        combine(
            wearableComponent.connectionChannelHelper.getState(),
            wearableComponent.connectionTester.getState(),
            flowOf(ConnectStatusOuterClass.ConnectStatus.UNRECOGNIZED)
        ) { channelState, connectionTester, flipperConnectStatus ->
            info { "Receive $channelState, $connectionTester, $flipperConnectStatus" }
            channelState then connectionTester then flipperConnectStatus
        }.onEach { (channelState, connectionTester, flipperConnectStatus) ->
            wearableComponent.wearEmulateStateMachine.onStatesUpdate(
                channelState,
                connectionTester,
                flipperConnectStatus
            )
        }.launchIn(viewModelScope)
    }

    fun getWearEmulateState(): StateFlow<WearEmulateState> =
        wearableComponent.wearEmulateStateMachine.getStateFlow()

    fun onClickEmulate() = Unit

    fun onShortEmulate() = Unit

    fun onStopEmulate() = Unit

    override fun onCleared() {
        super.onCleared()
        wearableComponent.connectionChannelHelper.onClear()
    }
}
