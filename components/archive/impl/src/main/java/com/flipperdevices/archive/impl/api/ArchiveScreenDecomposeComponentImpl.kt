package com.flipperdevices.archive.impl.api

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.archive.impl.composable.ComposableArchive
import com.flipperdevices.archive.impl.model.ArchiveNavigationConfig
import com.flipperdevices.archive.impl.viewmodel.CategoryViewModel
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.archive.impl.viewmodel.SpeedViewModel
import com.flipperdevices.bottombar.handlers.ResetTabDecomposeHandler
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.rootscreen.api.LocalRootNavigation
import com.flipperdevices.rootscreen.model.RootScreenConfig
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Provider

class ArchiveScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<ArchiveNavigationConfig>,
    private val synchronizationUiApi: SynchronizationUiApi,
    private val generalTabviewModelProvider: Provider<GeneralTabViewModel>,
    private val categoryViewModelProvider: Provider<CategoryViewModel>,
    private val speedViewModelProvider: Provider<SpeedViewModel>,
) : ScreenDecomposeComponent(componentContext), ResetTabDecomposeHandler {
    private val requestScrollToTopFlow = MutableStateFlow(false)

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val tabViewModel = viewModelWithFactory(key = null) {
            generalTabviewModelProvider.get()
        }

        val keys by tabViewModel.getKeys().collectAsState()
        val favoriteKeys by tabViewModel.getFavoriteKeys().collectAsState()
        val synchronizationState by tabViewModel.getSynchronizationState().collectAsState()

        val categoryViewModel = viewModelWithFactory(key = null) {
            categoryViewModelProvider.get()
        }
        val speedViewModel = viewModelWithFactory(key = null) {
            speedViewModelProvider.get()
        }

        val categories by categoryViewModel.getCategoriesFlow().collectAsState()
        val deletedCategory by categoryViewModel.getDeletedFlow().collectAsState()
        val speed by speedViewModel.speedFlow.collectAsState(null)

        val lazyListState = rememberLazyListState()
        val requestScrollToTop by requestScrollToTopFlow.collectAsState()
        LaunchedEffect(requestScrollToTop) {
            if (requestScrollToTop) {
                lazyListState.animateScrollToItem(0)
                requestScrollToTopFlow.emit(false)
            }
        }

        val rootNavigation = LocalRootNavigation.current

        ComposableArchive(
            synchronizationUiApi = synchronizationUiApi,
            onOpenSearchScreen = {
                navigation.pushToFront(ArchiveNavigationConfig.OpenSearch)
            },
            onOpenKeyScreen = { flipperKeyPath ->
                rootNavigation.push(RootScreenConfig.OpenKey(flipperKeyPath))
            },
            onOpenCategory = { categoryType ->
                navigation.pushToFront(ArchiveNavigationConfig.OpenCategory(categoryType, null))
            },
            keys = keys,
            synchronizationState = synchronizationState,
            favoriteKeys = favoriteKeys,
            onRefresh = tabViewModel::refresh,
            cancelSynchronization = tabViewModel::cancelSynchronization,
            categories = categories,
            deletedCategory = deletedCategory,
            lazyListState = lazyListState,
            speed = speed,
        )
    }

    override fun onResetTab() {
        requestScrollToTopFlow.update { true }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<ArchiveNavigationConfig>
        ): ArchiveScreenDecomposeComponentImpl
    }
}
