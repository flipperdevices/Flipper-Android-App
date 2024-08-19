package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.model.ServerRemoteControlParam
import com.flipperdevices.remotecontrols.grid.remote.api.RemoteGridScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.RemoteGridComposable
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, RemoteGridScreenDecomposeComponent.Factory::class)
class RemoteGridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: ServerRemoteControlParam,
    @Assisted onBack: DecomposeOnBackParameter,
    @Assisted onSaveKey: (NotSavedFlipperKey) -> Unit,
    remoteGridComponentFactory: RemoteGridComponent.Factory
) : RemoteGridScreenDecomposeComponent(componentContext) {
    private val gridComponent = remoteGridComponentFactory.invoke(
        componentContext = childContext("GridComponent"),
        param = param,
        onBack = onBack,
        onSaveKey = onSaveKey
    )

    @Composable
    override fun Render() {
        RemoteGridComposable(gridComponent)
    }
}
