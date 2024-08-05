package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.flow.StateFlow

interface SaveTempSignalApi : InstanceKeeper.Instance {

    val state: StateFlow<State>

    fun saveFile(
        textContent: String,
        nameWithExtension: String,
        extFolderPath: String
    )

    sealed interface State {
        data object Pending : State
        data object Error : State
        data class Uploading(val progressInternal: Long, val total: Long) : State {
            val progress: Float = if (total == 0L) 0f else progressInternal / total.toFloat()
        }

        data object Uploaded : State
    }
}
