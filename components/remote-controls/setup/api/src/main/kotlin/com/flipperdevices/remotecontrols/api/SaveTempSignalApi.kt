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

    fun saveFiles(vararg filesDesc: FileDesc, onFinished: () -> Unit = {})

    sealed interface State {
        data object Pending : State
        data class Uploading(val progressPercent: Float) : State

        data object Uploaded : State
    }
}
