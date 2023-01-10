package com.flipperdevices.wearable.emulate.impl.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.flipperdevices.wearable.emulate.common.WearableCommandInputStream
import com.flipperdevices.wearable.emulate.common.ipcemulate.Main
import com.flipperdevices.wearable.emulate.common.ipcemulate.requests.Emulate.EmulateStatus
import com.flipperdevices.wearable.emulate.impl.di.WearGraph
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

interface EmulateStateListener {
    fun getState(): StateFlow<EmulateStatus>
}

@ContributesBinding(WearGraph::class, EmulateStateListener::class)
class EmulateStateListenerImpl @Inject constructor(
    private val commandInputStream: WearableCommandInputStream<Main.MainResponse>,
    private val lifecycleOwner: LifecycleOwner
) : EmulateStateListener {
    override fun getState(): StateFlow<EmulateStatus> =
        commandInputStream.getRequestsFlow().filter { it.hasEmulateStatus() }
            .map { it.emulateStatus }
            .stateIn(
                lifecycleOwner.lifecycleScope,
                SharingStarted.WhileSubscribed(),
                EmulateStatus.UNRECOGNIZED
            )
}
