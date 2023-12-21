package com.flipperdevices.archive.category.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.archive.category.composable.ComposableCategory
import com.flipperdevices.archive.category.composable.ComposableDeleted
import com.flipperdevices.archive.category.model.CategoryNavigationConfig
import com.flipperdevices.archive.category.viewmodels.CategoryViewModel
import com.flipperdevices.archive.category.viewmodels.DeleteViewModel
import com.flipperdevices.archive.model.CategoryType
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.bridge.synchronization.api.SynchronizationUiApi
import com.flipperdevices.core.ui.ktx.viewModelWithFactory
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

class CategoryScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val categoryType: CategoryType,
    @Assisted private val navigation: StackNavigation<CategoryNavigationConfig>,
    private val deleteViewModelProvider: Provider<DeleteViewModel>,
    private val categoryViewModelFactory: CategoryViewModel.Factory,
    private val synchronizationUiApi: SynchronizationUiApi
) : DecomposeComponent, ComponentContext by componentContext {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val deleteViewModel = viewModelWithFactory(key = null) {
            deleteViewModelProvider.get()
        }
        val categoryViewModel = viewModelWithFactory(key = categoryType.toString()) {
            categoryViewModelFactory(categoryType)
        }
        val categoryState by categoryViewModel.getState().collectAsState()
        val synchronizationState by categoryViewModel.getSynchronizationState().collectAsState()

        val onOpenKeyScreen: (FlipperKeyPath) -> Unit = { flipperKeyPath ->
            navigation.push(CategoryNavigationConfig.OpenKey(flipperKeyPath))
        }

        when (categoryType) {
            is CategoryType.ByFileType -> ComposableCategory(
                categoryType = categoryType,
                synchronizationUiApi = synchronizationUiApi,
                onBack = navigation::pop,
                onOpenKeyScreen = onOpenKeyScreen,
                categoryState = categoryState,
                synchronizationState = synchronizationState
            )

            CategoryType.Deleted -> ComposableDeleted(
                onBack = navigation::pop,
                onOpenKeyScreen = onOpenKeyScreen,
                onRestoreAll = deleteViewModel::onRestoreAll,
                onDeleteAll = deleteViewModel::onDeleteAll,
                categoryState = categoryState,
                synchronizationState = synchronizationState
            )
        }
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            categoryType: CategoryType,
            navigation: StackNavigation<CategoryNavigationConfig>
        ): CategoryScreenDecomposeComponentImpl
    }
}
