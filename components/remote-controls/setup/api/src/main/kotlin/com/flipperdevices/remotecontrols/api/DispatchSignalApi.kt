package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.flipperdevices.bridge.dao.api.model.FlipperFilePath
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.keyemulate.model.EmulateConfig
import kotlinx.coroutines.flow.StateFlow

interface DispatchSignalApi : InstanceKeeper.Instance {
    val state: StateFlow<State>

    fun dismissBusyDialog()

    /**
     * Dispatch key from temporal file which contains only one key
     */
    fun dispatch(
        config: EmulateConfig,
        identifier: IfrKeyIdentifier,
        onDispatched: () -> Unit = {}
    )

    fun stopEmulate()

    fun reset()

    /**
     * Dispatch specific key from custom located remote
     */
    fun dispatch(
        identifier: IfrKeyIdentifier,
        isOneTime: Boolean = true,
        remotes: List<InfraredRemote>,
        ffPath: FlipperFilePath,
        onDispatched: () -> Unit = {}
    )

    sealed interface State {
        data object Pending : State
        data object FlipperIsBusy : State
        data object FlipperNotConnected : State
        data object FlipperNotSupported : State
        data class Emulating(val ifrKeyIdentifier: IfrKeyIdentifier) : State
        data object Error : State
    }
}
