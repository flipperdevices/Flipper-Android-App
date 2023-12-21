package com.flipperdevices.archive.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.archive.impl.composable.ComposableArchive
import com.flipperdevices.archive.impl.model.ArchiveNavigationConfig
import com.flipperdevices.archive.impl.viewmodel.CategoryViewModel
import com.flipperdevices.archive.impl.viewmodel.GeneralTabViewModel
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class ArchiveScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<ArchiveNavigationConfig>,
    private val synchronizationUiApi: SynchronizationUiApi,
    private val generalTabviewModelProvider: Provider<GeneralTabViewModel>,
    private val categoryViewModelProvider: Provider<CategoryViewModel>
) : DecomposeComponent, ComponentContext by componentContext {

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

        val categories by categoryViewModel.getCategoriesFlow().collectAsState()
        val deletedCategory by categoryViewModel.getDeletedFlow().collectAsState()

        ComposableArchive(
            synchronizationUiApi = synchronizationUiApi,
            onOpenSearchScreen = {
                navigation.push(ArchiveNavigationConfig.OpenSearch)
            },
            onOpenKeyScreen = { flipperKeyPath ->
                navigation.push(ArchiveNavigationConfig.OpenKey(flipperKeyPath))
            },
            onOpenCategory = { categoryType ->
                navigation.push(ArchiveNavigationConfig.OpenCategory(categoryType))
            },
            keys = keys,
            synchronizationState = synchronizationState,
            favoriteKeys = favoriteKeys,
            onRefresh = tabViewModel::refresh,
            cancelSynchronization = tabViewModel::cancelSynchronization,
            categories = categories,
            deletedCategory = deletedCategory
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<ArchiveNavigationConfig>
        ): ArchiveScreenDecomposeComponentImpl
    }
}
