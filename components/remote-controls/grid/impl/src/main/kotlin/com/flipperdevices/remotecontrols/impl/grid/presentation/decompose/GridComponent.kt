package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.bridge.synchronization.api.SynchronizationState
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.infrared.editor.core.model.InfraredRemote
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

interface GridComponent {
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
            val synchronizationState: SynchronizationState
        ) : Model {
            val isSynchronizing = synchronizationState is SynchronizationState.InProgress
        }

        data object Error : Model
    }

    fun interface Factory {
        fun invoke(
            componentContext: ComponentContext,
            param: GridScreenDecomposeComponent.Param,
            onPopClick: () -> Unit
        ): GridComponent
    }
}
