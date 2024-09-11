package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.faphub.errors.api.throwable.FapHubError
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.api.InfraredConnectionApi.InfraredEmulateState
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.api.SaveTempSignalApi
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.viewmodel.RemoteGridViewModel.State
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface RemoteGridComponent {
    val param: ServerRemoteControlParam

    fun model(coroutineScope: CoroutineScope): StateFlow<Model>

    fun onButtonClick(identifier: IfrKeyIdentifier)
    fun tryLoad()

    fun pop()

    fun dismissDialog()

    fun save()

    sealed interface Model {
        data class Loading(
            val progress: Float = 0f,
        ) : Model

        data class Loaded(
            val pagesLayout: PagesLayout,
            val remotes: ImmutableList<InfraredRemote>,
            val flipperDialog: FlipperDispatchDialogApi.DialogType? = null,
            val emulatedKey: IfrKeyIdentifier? = null,
            val saveState: SaveTempSignalApi.State,
            val connectionState: InfraredEmulateState
        ) : Model {
            val isConnected = connectionState != InfraredEmulateState.NOT_CONNECTED
            val isSavingFiles = saveState is SaveTempSignalApi.State.Uploading
            val saveProgressOrNull = (saveState as? SaveTempSignalApi.State.Uploading)
                ?.progressPercent
                ?.times(other = 100)
                ?.toInt()
                ?.coerceIn(minimumValue = 0, maximumValue = 100)
        }

        data class Error(val throwable: FapHubError) : Model

        val isFilesSaved: Boolean
            get() = this is Loaded && !this.isSavingFiles
    }

    fun interface Factory {
        fun invoke(
            componentContext: ComponentContext,
            param: ServerRemoteControlParam,
            onBack: DecomposeOnBackParameter,
            onSaveKey: (NotSavedFlipperKey) -> Unit
        ): RemoteGridComponent
    }
}
