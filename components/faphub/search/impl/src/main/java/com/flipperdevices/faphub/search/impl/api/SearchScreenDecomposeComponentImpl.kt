package com.flipperdevices.faphub.search.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.faphub.errors.api.FapHubComposableErrorsRenderer
import com.flipperdevices.faphub.installation.button.api.FapButtonSize
import com.flipperdevices.faphub.installation.button.api.FapInstallationUIApi
import com.flipperdevices.faphub.installation.button.api.toFapButtonConfig
import com.flipperdevices.faphub.search.impl.composable.ComposableSearchScreen
import com.flipperdevices.faphub.search.impl.model.FapHubSearchNavigationConfig
import com.flipperdevices.faphub.search.impl.viewmodel.FapHubSearchViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Provider

@Suppress("LongParameterList")
class SearchScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<FapHubSearchNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    private val fapInstallationUIApi: FapInstallationUIApi,
    private val errorsRenderer: FapHubComposableErrorsRenderer,
    private val metricApi: MetricApi,
    private val searchViewModelProvider: Provider<FapHubSearchViewModel>
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val searchViewModel = viewModelWithFactory(key = null) {
            searchViewModelProvider.get()
        }
        val fapsList = searchViewModel.faps.collectAsLazyPagingItems()
        val searchRequest by searchViewModel.getSearchRequest().collectAsState()

        ComposableSearchScreen(
            onBack = onBack::invoke,
            onFapItemClick = {
                metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_APP, it.name)
                navigation.pushToFront(FapHubSearchNavigationConfig.FapScreen(it.id))
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
            fapsList = fapsList,
            onChangeText = searchViewModel::onChangeSearchText,
            searchRequest = searchRequest
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<FapHubSearchNavigationConfig>,
            onBack: DecomposeOnBackParameter
        ): SearchScreenDecomposeComponentImpl
    }
}
