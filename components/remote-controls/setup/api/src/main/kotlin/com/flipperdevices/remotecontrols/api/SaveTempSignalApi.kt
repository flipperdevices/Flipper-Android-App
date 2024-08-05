package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.flow.StateFlow

interface SaveTempSignalApi : InstanceKeeper.Instance {

    val state: StateFlow<State>

    class FileDesc(
        val textContent: String,
        val nameWithExtension: String,
        val extFolderPath: String
    )

    fun saveFile(vararg desc: FileDesc)

    sealed interface State {
        data object Pending : State
        data object Error : State
        data class Uploading(val progressInternal: Long, val total: Long) : State {
            val progress: Float = if (total == 0L) 0f else progressInternal / total.toFloat()
        }

        data object Uploaded : State
    }
}
