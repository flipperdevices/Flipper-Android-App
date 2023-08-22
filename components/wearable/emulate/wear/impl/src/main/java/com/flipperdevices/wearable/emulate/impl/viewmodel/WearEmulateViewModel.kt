package com.flipperdevices.wearable.emulate.impl.viewmodel

import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.wearable.emulate.impl.api.EMULATE_PATH_KEY
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import tangle.inject.TangleParam
import tangle.viewmodel.VMInject

class WearEmulateViewModel @VMInject constructor(
    @TangleParam(EMULATE_PATH_KEY)
    private val keyPath: String
) : LifecycleViewModel(),
    LogTagProvider {
    override val TAG = "WearEmulateViewModel"

    fun getWearEmulateState(): StateFlow<WearEmulateState> = MutableStateFlow(WearEmulateState.NotInitialized)

    fun onClickEmulate() = Unit

    fun onShortEmulate() = Unit

    fun onStopEmulate() = Unit

    override fun onCleared() {
        super.onCleared()
    }

}
