package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

internal interface GridComponent {
    fun model(coroutineScope: CoroutineScope): StateFlow<Model>

    fun onButtonClicked(identifier: IfrKeyIdentifier)
    fun tryLoad()
    fun pop()

    sealed interface Model {
        data class Loading(val progress: Float) : Model
        data class Loaded(
            val pagesLayout: PagesLayout,
        ) : Model

        data object Error : Model
    }

    fun interface Factory {
        fun create(
            componentContext: ComponentContext,
            param: GridScreenDecomposeComponent.Param,
            onPopClicked: () -> Unit
        ): GridComponent
    }
}
