package com.flipperdevices.wearable.sync.wear.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.wearable.sync.wear.api.KeysListDecomposeComponent
import com.flipperdevices.wearable.sync.wear.impl.composable.ComposableKeysList
import com.flipperdevices.wearable.sync.wear.impl.viewmodel.KeysListViewModel
import com.flipperdevices.wearrootscreen.model.WearRootConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import me.gulya.anvil.assisted.ContributesAssistedFactory
import javax.inject.Provider

@ContributesAssistedFactory(AppGraph::class, KeysListDecomposeComponent.Factory::class)
class KeysListDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<WearRootConfig>,
    private val keysListViewModelProvider: Provider<KeysListViewModel>
) : KeysListDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableKeysList(
            onKeyOpen = {
                navigation.pushToFront(WearRootConfig.OpenKey(it.path))
            },
            keysListViewModel = viewModelWithFactory(key = null) {
                keysListViewModelProvider.get()
            }
        )
    }
}
