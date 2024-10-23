package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.remotecontrols.api.FlipperDispatchDialogApi
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.composable.SetupScreen
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@Suppress("LongParameterList")
@ContributesAssistedFactory(AppGraph::class, SetupScreenDecomposeComponent.Factory::class)
class SetupScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted param: SetupScreenDecomposeComponent.Param,
    @Assisted onBack: () -> Unit,
    @Assisted onIrFileReady: (id: Long, name: String) -> Unit,
    setupComponentFactory: SetupComponent.Factory,
    flipperDispatchDialogApiFactory: FlipperDispatchDialogApi.Factory,
    private val errorsRenderer: FapHubComposableErrorsRenderer,
) : SetupScreenDecomposeComponent(componentContext) {
    private val setupComponent = setupComponentFactory.createSetupComponent(
        componentContext = childContext("SetupComponent"),
        param = param,
        onBack = onBack,
        onIrFileReady = onIrFileReady
    )
    private val flipperDispatchDialogApi = flipperDispatchDialogApiFactory.invoke(onBack = onBack)

    @Composable
    override fun Render() {
        SetupScreen(
            setupComponent = setupComponent,
            errorsRenderer = errorsRenderer,
            flipperDispatchDialogApi = flipperDispatchDialogApi
        )
    }
}
