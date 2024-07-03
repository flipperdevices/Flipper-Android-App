package com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.internal

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.flipperdevices.remotecontrols.impl.setup.presentation.decompose.SetupComponent
import com.flipperdevices.remotecontrols.api.SetupScreenDecomposeComponent
import com.flipperdevices.remotecontrols.impl.setup.composable.SetupScreen

internal class SetupScreenDecomposeComponentImpl(
    componentContext: ComponentContext,
    setupComponentFactory: SetupComponent.Factory,
    param: SetupScreenDecomposeComponent.Param,
    onBack: () -> Unit,
    onIfrFileFound: (ifrFileId: Long) -> Unit
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
