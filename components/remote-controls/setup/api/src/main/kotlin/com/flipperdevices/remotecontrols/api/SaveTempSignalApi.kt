package com.flipperdevices.remotecontrols.api

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.flipperdevices.bridge.dao.api.model.FlipperFileFormat
import com.flipperdevices.deeplink.model.DeeplinkContent
import kotlinx.coroutines.flow.StateFlow

interface SaveTempSignalApi : InstanceKeeper.Instance {

    val state: StateFlow<State>

    fun saveFile(
        deeplinkContent: DeeplinkContent,
        nameWithExtension: String,
        folderName: String = DEFAULT_FOLDER_NAME
    )

    sealed interface State {
        data object Pending : State
        data object Error : State
        data class Uploading(val progressInternal: Long, val total: Long) : State {
            val progress: Float = if (total == 0L) 0f else progressInternal / total.toFloat()
        }

        data object Uploaded : State
    }

    companion object {
        private const val DEFAULT_FOLDER_NAME = "temp"

        fun SaveTempSignalApi.saveFile(
            fff: FlipperFileFormat,
            nameWithExtension: String,
            folderName: String = DEFAULT_FOLDER_NAME
        ) = saveFile(
            folderName = folderName,
            nameWithExtension = nameWithExtension,
            deeplinkContent = DeeplinkContent.FFFContent(
                filename = nameWithExtension,
                flipperFileFormat = fff
            ),
        )
    }
}
