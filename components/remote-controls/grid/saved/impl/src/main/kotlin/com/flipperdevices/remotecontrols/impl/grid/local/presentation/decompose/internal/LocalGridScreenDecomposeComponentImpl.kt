package com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.remotecontrols.impl.grid.local.api.LocalGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.local.composable.LocalGridComposable
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, LocalGridScreenDecomposeComponent.Factory::class)
class LocalGridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: GridControlParam.Path,
    @Assisted onBack: DecomposeOnBackParameter,
    localGridComponentFactory: LocalGridComponent.Factory
) : LocalGridScreenDecomposeComponent(componentContext) {
    private val localGridComponent = localGridComponentFactory.invoke(
        componentContext = childContext("GridComponent_local"),
        param = param,
        onBack = onBack,
    )

    @Composable
    override fun Render() {
        LocalGridComposable(localGridComponent)
    }
}
