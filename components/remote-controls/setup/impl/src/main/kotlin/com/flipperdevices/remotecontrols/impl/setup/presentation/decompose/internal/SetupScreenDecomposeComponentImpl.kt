package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.composable.SetupScreen
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, SetupScreenDecomposeComponent.Factory::class)
class SetupScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: SetupScreenDecomposeComponent.Param,
    @Assisted onBack: () -> Unit,
    @Assisted onIfrFileFound: (ifrFileId: Long) -> Unit,
    setupComponentFactory: SetupComponent.Factory,
) : SetupScreenDecomposeComponent(componentContext) {
    private val setupComponent = setupComponentFactory.createSetupComponent(
        componentContext = childContext("SetupComponent"),
        param = param,
        onBack = onBack,
        onIfrFileFound = onIfrFileFound
    )

    @Composable
    override fun Render() {
        SetupScreen(setupComponent = setupComponent)
    }
}
