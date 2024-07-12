package com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.GridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.composable.GridComposable
import com.flipperdevices.remotecontrols.impl.grid.presentation.decompose.GridComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, GridScreenDecomposeComponent.Factory::class)
class GridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: GridScreenDecomposeComponent.Param,
    @Assisted onPopClicked: () -> Unit,
    gridComponentFactory: GridComponent.Factory
) : GridScreenDecomposeComponent(componentContext) {
    private val gridComponent = gridComponentFactory.invoke(
        componentContext = childContext("GridComponent"),
        param = param,
        onPopClicked = onPopClicked
    )

    @Composable
    override fun Render() {
        GridComposable(gridComponent)
    }
}
