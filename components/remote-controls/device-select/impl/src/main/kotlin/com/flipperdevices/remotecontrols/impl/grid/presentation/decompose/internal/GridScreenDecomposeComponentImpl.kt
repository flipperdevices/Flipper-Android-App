package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.composable.GridComposable
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent

internal class GridScreenDecomposeComponentImpl(
    componentContext: ComponentContext,
    param: GridScreenDecomposeComponent.Param,
    gridComponentFactory: GridComponent.Factory,
    onPopClicked: () -> Unit
) : GridScreenDecomposeComponent(componentContext) {
    private val gridComponent = gridComponentFactory.create(
        componentContext = childContext("GridComponent"),
        param = param,
        onPopClicked = onPopClicked
    )

    @Composable
    override fun Render() {
        GridComposable(gridComponent)
    }
}
