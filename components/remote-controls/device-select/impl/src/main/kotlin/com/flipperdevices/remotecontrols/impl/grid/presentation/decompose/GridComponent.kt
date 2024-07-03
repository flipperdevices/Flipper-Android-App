package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose

import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.ifrmvp.model.PagesLayout
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import kotlinx.coroutines.flow.StateFlow

internal interface GridComponent {
    val model: StateFlow<Model>

    fun onButtonClicked(identifier: IfrKeyIdentifier)
    fun tryLoad()
    fun pop()

    sealed interface Model {
        data object Loading : Model
        data class Loaded(val pagesLayout: PagesLayout) : Model
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
