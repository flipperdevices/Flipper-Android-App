package com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.remotecontrols.api.model.GridControlParam
import com.flipperdevices.remotecontrols.impl.grid.local.composable.LocalGridComposable
import com.flipperdevices.remotecontrols.impl.grid.local.presentation.decompose.LocalGridComponent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class LocalGridScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: GridControlParam.Path,
    @Assisted onBack: DecomposeOnBackParameter,
    localGridComponentFactory: LocalGridComponent.Factory
) : ScreenDecomposeComponent(componentContext) {
    private val localGridComponent = localGridComponentFactory.invoke(
        componentContext = childContext("GridComponent_local"),
        param = param,
        onBack = onBack,
    )

    @Composable
    override fun Render() {
        LocalGridComposable(localGridComponent)
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            param: GridControlParam.Path,
            onBack: DecomposeOnBackParameter,
        ): LocalGridScreenDecomposeComponentImpl
    }
}
