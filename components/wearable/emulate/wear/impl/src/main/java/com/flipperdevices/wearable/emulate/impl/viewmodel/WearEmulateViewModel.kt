package com.flipperdevices.wearable.emulate.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.LifecycleViewModel
import com.flipperdevices.wearable.emulate.impl.api.EMULATE_PATH_KEY
import com.flipperdevices.wearable.emulate.impl.di.DaggerWearEmulateComponent
import com.flipperdevices.wearable.emulate.impl.model.WearEmulateState
import java.io.File
import kotlinx.coroutines.flow.StateFlow
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

    private val keyType by lazy { FlipperKeyType.getByExtension(File(keyPath).extension) }

    init {
        /*combine(
            wearableComponent.connectionChannelHelper.getState(),
            flowOf("")
        ) {

        }*/
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
