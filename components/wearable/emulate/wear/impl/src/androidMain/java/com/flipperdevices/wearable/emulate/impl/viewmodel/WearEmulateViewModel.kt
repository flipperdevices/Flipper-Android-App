package com.flipperdevices.wearable.emulate.impl.viewmodel

import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.dao.api.model.FlipperKeyType
import com.flipperdevices.core.log.LogTagProvider
import com.flipperdevices.core.ui.lifecycle.DecomposeViewModel
import com.flipperdevices.wearable.emulate.api.ChannelClientHelper
import com.flipperdevices.wearable.emulate.impl.helper.ConnectionHelper
import com.flipperdevices.wearable.emulate.impl.helper.EmulateHelper
import com.flipperdevices.wearable.emulate.impl.helper.FlipperStatusHelper
import com.flipperdevices.wearable.emulate.impl.helper.WearStateMachineHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import java.io.File

class WearEmulateViewModel @AssistedInject constructor(
    @Assisted private val keyPath: FlipperKeyPath,
    private val wearStateMachineHelper: WearStateMachineHelper,
    channelClientHelper: ChannelClientHelper,
    connectionHelper: ConnectionHelper,
    flipperStatusHelper: FlipperStatusHelper,
    private val emulateHelper: EmulateHelper
) : DecomposeViewModel(),
    LogTagProvider {
    override val TAG = "WearEmulateViewModel"

    private val keyToEmulate = KeyToEmulate(keyPath.path.pathToKey)

    init {
        combine(
            channelClientHelper.getState(),
            connectionHelper.getState(),
            flipperStatusHelper.getState(),
            emulateHelper.getState()
        ) { channelState, connectionState, flipperState, emulateState ->
            wearStateMachineHelper.onStatesUpdated(
                channelState,
                connectionState,
                flipperState,
                emulateState,
                keyToEmulate
            )
        }.launchIn(viewModelScope)
    }

    fun getWearEmulateState() = wearStateMachineHelper.getState()

    fun onClickEmulate() = emulateHelper.onClickEmulate(keyToEmulate)

    fun onShortEmulate() = emulateHelper.onShortEmulate(keyToEmulate)

    fun onStopEmulate() = emulateHelper.onStopEmulate()

    override fun onDestroy() {
        super.onDestroy()
        emulateHelper.onStopEmulate()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            keyPath: FlipperKeyPath
        ): WearEmulateViewModel
    }
}

enum class WearLoadingState {
    INITIALIZING,
    FINDING_PHONE,
    NOT_FOUND_PHONE,
    CONNECTING_PHONE,
    TEST_CONNECTION,
    CONNECTING_FLIPPER
}

data class KeyToEmulate(
    val keyPath: String
) {
    val keyType: FlipperKeyType?
        get() = FlipperKeyType.getByExtension(File(keyPath).extension)
}
