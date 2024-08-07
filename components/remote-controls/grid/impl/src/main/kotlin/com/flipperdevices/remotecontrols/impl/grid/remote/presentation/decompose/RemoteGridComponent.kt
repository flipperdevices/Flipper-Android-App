package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface RemoteGridComponent {
    fun model(coroutineScope: CoroutineScope): StateFlow<Model>

    fun onButtonClick(identifier: IfrKeyIdentifier)
    fun tryLoad()

    fun pop()

    fun dismissBusyDialog()

    fun save()

    sealed interface Model {
        data class Loading(
            val progress: Float = 0f,
        ) : Model

        data class Loaded(
            val pagesLayout: PagesLayout,
            val remotes: ImmutableList<InfraredRemote>,
            val isFlipperBusy: Boolean = false,
            val emulatedKey: IfrKeyIdentifier? = null,
            val isSavingFiles: Boolean,
        ) : Model

        data object Error : Model

        val isFilesSaved: Boolean
            get() = this is Loaded && !this.isSavingFiles
    }

    fun interface Factory {
        fun invoke(
            componentContext: ComponentContext,
            param: GridControlParam.Id,
            onBack: DecomposeOnBackParameter,
            onSaveKey: (NotSavedFlipperKey) -> Unit
        ): RemoteGridComponent
    }
}
