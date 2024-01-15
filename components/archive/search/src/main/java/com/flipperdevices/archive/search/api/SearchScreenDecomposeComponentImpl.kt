package com.flipperdevices.archive.search.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.archive.api.SelectKeyPathListener
import com.flipperdevices.archive.search.composable.ComposableSearch
import com.flipperdevices.archive.search.model.SearchNavigationConfig
import com.flipperdevices.archive.search.viewmodel.SearchViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class SearchScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val onItemSelected: SelectKeyPathListener?,
    @Assisted private val navigation: StackNavigation<SearchNavigationConfig>,
    private val searchViewModelProvider: Provider<SearchViewModel>,
    private val synchronizationUiApi: SynchronizationUiApi,
) : ScreenDecomposeComponent(componentContext) {
    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val keyboard = LocalSoftwareKeyboardController.current
        val searchViewModel: SearchViewModel = viewModelWithFactory(key = null) {
            searchViewModelProvider.get()
        }

        val rootNavigation = LocalRootNavigation.current

        ComposableSearch(
            searchViewModel = searchViewModel,
            synchronizationUiApi = synchronizationUiApi,
            onBack = {
                keyboard?.hide()
                navigation.pop()
            },
            onOpenKeyScreen = { flipperKeyPath ->
                keyboard?.hide()
                if (onItemSelected != null) {
                    onItemSelected.invoke(flipperKeyPath)
                } else {
                    rootNavigation.push(RootScreenConfig.OpenKey(flipperKeyPath))
                }
            }
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemSelected: SelectKeyPathListener?,
            navigation: StackNavigation<SearchNavigationConfig>
        ): SearchScreenDecomposeComponentImpl
    }
}
