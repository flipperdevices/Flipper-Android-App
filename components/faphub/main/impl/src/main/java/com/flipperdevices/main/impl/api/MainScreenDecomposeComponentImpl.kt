package com.flipperdevices.main.impl.api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pushToFront
import com.flipperdevices.core.ui.lifecycle.viewModelWithFactory
import com.flipperdevices.deeplink.model.Deeplink
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.installedtab.api.FapInstalledApi
import com.flipperdevices.main.impl.composable.ComposableFapHubMainScreen
import com.flipperdevices.main.impl.model.FapHubNavigationConfig
import com.flipperdevices.main.impl.viewmodel.MainViewModel
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.ui.decompose.DecomposeOnBackParameter
import com.flipperdevices.ui.decompose.ScreenDecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

@Suppress("LongParameterList")
class MainScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<FapHubNavigationConfig>,
    @Assisted private val onBack: DecomposeOnBackParameter,
    @Assisted private val deeplink: Deeplink.BottomBar.HubTab.FapHub.MainScreen?,
    private val catalogTabApi: CatalogTabApi,
    private val installedApi: FapInstalledApi,
    private val metricApi: MetricApi,
    private val mainViewModelFactory: MainViewModel.Factory
) : ScreenDecomposeComponent(componentContext) {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val mainViewModel = viewModelWithFactory(key = deeplink) {
            mainViewModelFactory(deeplink)
        }
        val selectedTab by mainViewModel.getTabFlow().collectAsState()

        val readyToUpdateCount = installedApi.getUpdatePendingCount(this)

        ComposableFapHubMainScreen(
            onBack = onBack::invoke,
            catalogTabComposable = {
                catalogTabApi.ComposableCatalogTab(
                    onOpenFapItem = {
                        metricApi.reportSimpleEvent(
                            SimpleEvent.OPEN_FAPHUB_APP,
                            it.applicationAlias
                        )
                        navigation.pushToFront(FapHubNavigationConfig.FapScreen(it.id))
                    },
                    onCategoryClick = {
                        metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_CATEGORY, it.name)
                        navigation.pushToFront(FapHubNavigationConfig.Category(it))
                    },
                    componentContext = this
                )
            },
            installedTabComposable = {
                installedApi.ComposableInstalledTab(
                    onOpenFapItem = {
                        metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_APP, it)
                        navigation.pushToFront(FapHubNavigationConfig.FapScreen(it))
                    },
                    componentContext = this
                )
            },
            onOpenSearch = {
                metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_SEARCH)
                navigation.pushToFront(FapHubNavigationConfig.Search)
            },
            installedNotificationCount = readyToUpdateCount,
            selectedTab = selectedTab,
            onSelect = mainViewModel::onSelectTab
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<FapHubNavigationConfig>,
            onBack: DecomposeOnBackParameter,
            deeplink: Deeplink.BottomBar.HubTab.FapHub.MainScreen?
        ): MainScreenDecomposeComponentImpl
    }
}
