package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.model.InfraredRemote
import com.flipperdevices.keyemulate.model.EmulateConfig
import kotlinx.coroutines.flow.StateFlow

interface DispatchSignalApi : InstanceKeeper.Instance {
    val state: StateFlow<State>

    /**
     * Reset current state and stop emulate
     */
    fun reset()

    /**
     * Dispatch key from temporal file which contains only one key
     */
    fun dispatch(config: EmulateConfig)

    /**
     * Dispatch specific key from custom located remote
     */
    fun dispatch(
        identifier: IfrKeyIdentifier,
        remotes: List<InfraredRemote>,
        fileName: String
    )

    sealed interface State {
        data object Pending : State
        data object Emulating : State
        data object Error : State
    }
}
