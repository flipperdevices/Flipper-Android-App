package com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.bridge.dao.api.model.FlipperKey
import com.flipperdevices.keyedit.api.NotSavedFlipperKey
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.remotecontrols.impl.grid.remote.composable.RemoteGridComposable
import com.flipperdevices.remotecontrols.impl.grid.remote.presentation.decompose.RemoteGridComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class RemoteGridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: GridControlParam.Id,
    @Assisted onBack: DecomposeOnBackParameter,
    @Assisted onSaveKey: (NotSavedFlipperKey) -> Unit,
    remoteGridComponentFactory: RemoteGridComponent.Factory
) : ScreenDecomposeComponent(componentContext) {
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

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: GridControlParam.Id,
            onBack: DecomposeOnBackParameter,
            onSaveKey: (NotSavedFlipperKey) -> Unit
        ): RemoteGridScreenDecomposeComponentImpl
    }
}
