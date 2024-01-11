package com.flipperdevices.wearable.sync.wear.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.wearable.sync.wear.api.KeysListDecomposeComponent
import com.flipperdevices.wearable.sync.wear.impl.composable.ComposableKeysList
import com.flipperdevices.wearable.sync.wear.impl.viewmodel.KeysListViewModel
import com.flipperdevices.wearrootscreen.model.WearRootConfig
import com.squareup.anvil.annotations.ContributesBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class KeysListDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<WearRootConfig>,
    private val keysListViewModelProvider: Provider<KeysListViewModel>
) : KeysListDecomposeComponent(), ComponentContext by componentContext {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        ComposableKeysList(
            onKeyOpen = {
                navigation.push(WearRootConfig.OpenKey(it.path))
            },
            keysListViewModel = viewModelWithFactory(key = null) {
                keysListViewModelProvider.get()
            }
        )
    }

    @AssistedFactory
    @ContributesBinding(AppGraph::class, KeysListDecomposeComponent.Factory::class)
    interface Factory : KeysListDecomposeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<WearRootConfig>
        ): KeysListDecomposeComponentImpl
    }
}
