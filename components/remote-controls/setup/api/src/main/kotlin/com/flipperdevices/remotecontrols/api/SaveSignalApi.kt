package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import kotlinx.coroutines.flow.StateFlow

interface SaveSignalApi : InstanceKeeper.Instance {

    val state: StateFlow<State>

    fun save(fff: FlipperFileFormat, filePath: String)

    sealed interface State {
        data object Pending : State
        data object Error : State
        data class Uploading(val progressInternal: Long, val total: Long) : State {
            val progress: Float = if (total == 0L) 0f else progressInternal / total.toFloat()
        }

        data object Uploaded : State
    }
}
