package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.keyemulate.model.EmulateConfig
import kotlinx.coroutines.flow.StateFlow

interface DispatchSignalApi : InstanceKeeper.Instance {
    val state: StateFlow<State>

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
        ffPath: FlipperFilePath
    )

    sealed interface State {
        data object Pending : State
        data object Emulating : State
        data object Error : State
    }
}
