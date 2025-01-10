package com.flipperdevices.bridge.connection.screens.search

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.flipperdevices.core.di.AppGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory

@ContributesAssistedFactory(AppGraph::class, ConnectionSearchDecomposeComponent.Factory::class)
class ConnectionSearchDecomposeComponentNoop @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
) : ConnectionSearchDecomposeComponent(componentContext) {

    @Composable
    override fun Render() {
    }
}