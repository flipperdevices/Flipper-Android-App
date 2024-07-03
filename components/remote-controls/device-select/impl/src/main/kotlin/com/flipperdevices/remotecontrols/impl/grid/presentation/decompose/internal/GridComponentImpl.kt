package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import com.flipperdevices.remotecontrols.impl.grid.presentation.viewmodel.GridViewModel
import com.flipperdevices.ifrmvp.model.IfrKeyIdentifier
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import kotlinx.coroutines.flow.asStateFlow

internal class GridComponentImpl(
    componentContext: ComponentContext,
    param: GridScreenDecomposeComponent.Param,
    createGridViewModel: (param: GridScreenDecomposeComponent.Param) -> GridViewModel,
    private val onPopClicked: () -> Unit
) : GridComponent, ComponentContext by componentContext {
    private val gridFeature = instanceKeeper.getOrCreate {
        createGridViewModel.invoke(param)
    }
    override val model = gridFeature.model.asStateFlow()

    override fun onButtonClicked(identifier: IfrKeyIdentifier) {
    }

    override fun tryLoad() = gridFeature.tryLoad()
    override fun pop() = onPopClicked.invoke()
}
