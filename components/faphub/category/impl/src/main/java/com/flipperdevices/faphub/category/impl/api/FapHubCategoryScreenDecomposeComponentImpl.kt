package com.flipperdevices.faphub.category.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.category.impl.composable.ComposableFapHubCategory
import com.flipperdevices.faphub.category.impl.model.FapCategoryNavigationConfig
import com.flipperdevices.faphub.category.impl.viewmodel.FapHubCategoryViewModel
import com.flipperdevices.faphub.dao.api.model.FapCategory
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class FapHubCategoryScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val fapCategory: FapCategory,
    @Assisted private val navigation: StackNavigation<FapCategoryNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val categoryViewModelFactory: FapHubCategoryViewModel.Factory,
    private val metricApi: MetricApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer,
    private val fapInstallationUIApi: FapInstallationUIApi
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val categoryViewModel = viewModelWithFactory(key = fapCategory.toString()) {
            categoryViewModelFactory(fapCategory)
        }
        val fapsList = categoryViewModel.getFapsFlow().collectAsLazyPagingItems()
        val sortType by categoryViewModel.getSortTypeFlow().collectAsState()

        ComposableFapHubCategory(
            onBack = onBack::invoke,
            onOpenSearch = {
                navigation.pushToFront(FapCategoryNavigationConfig.Search)
            },
            onOpenFapItem = {
                metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_APP, it.applicationAlias)
                navigation.pushToFront(FapCategoryNavigationConfig.FapScreen(it.id))
            },
            errorsRenderer = errorsRenderer,
            installationButton = { fapItem, modifier ->
                fapInstallationUIApi.ComposableButton(
                    config = fapItem?.toFapButtonConfig(),
                    modifier = modifier,
                    fapButtonSize = FapButtonSize.COMPACTED,
                    componentContext = this
                )
            },
            categoryName = fapCategory.name,
            onSelectSortType = categoryViewModel::onSelectSortType,
            fapsList = fapsList,
            sortType = sortType
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            fapCategory: FapCategory,
            navigation: StackNavigation<FapCategoryNavigationConfig>,
            onBack: DecomposeOnBackParameter,
        ): FapHubCategoryScreenDecomposeComponentImpl
    }
}
