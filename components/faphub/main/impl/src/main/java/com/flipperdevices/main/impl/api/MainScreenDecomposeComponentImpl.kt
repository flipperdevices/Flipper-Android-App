package com.flipperdevices.main.impl.api

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.installedtab.api.FapInstalledApi
import com.flipperdevices.main.impl.composable.ComposableFapHubMainScreen
import com.flipperdevices.main.impl.model.FapHubNavigationConfig
import com.flipperdevices.metric.api.MetricApi
import com.flipperdevices.metric.api.events.SimpleEvent
import com.flipperdevices.ui.decompose.DecomposeComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MainScreenDecomposeComponentImpl @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    @Assisted private val navigation: StackNavigation<FapHubNavigationConfig>,
    private val catalogTabApi: CatalogTabApi,
    private val installedApi: FapInstalledApi,
    private val metricApi: MetricApi
) : DecomposeComponent, ComponentContext by componentContext {

    @Composable
    @Suppress("NonSkippableComposable")
    override fun Render() {
        val readyToUpdateCount = installedApi.getUpdatePendingCount()
        ComposableFapHubMainScreen(
            onBack = navigation::pop,
            catalogTabComposable = {
                catalogTabApi.ComposableCatalogTab(
                    onOpenFapItem = {
                        metricApi.reportSimpleEvent(
                            SimpleEvent.OPEN_FAPHUB_APP,
                            it.applicationAlias
                        )
                        navigation.push(FapHubNavigationConfig.FapScreen(it.id))
                    },
                    onCategoryClick = {
                        metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_CATEGORY, it.name)
                        navigation.push(FapHubNavigationConfig.Category(it))
                    }
                )
            },
            installedTabComposable = {
                installedApi.ComposableInstalledTab(onOpenFapItem = {
                    metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_APP, it)
                    navigation.push(FapHubNavigationConfig.FapScreen(it))
                })
            },
            onOpenSearch = {
                metricApi.reportSimpleEvent(SimpleEvent.OPEN_FAPHUB_SEARCH)
                navigation.push(FapHubNavigationConfig.Search)
            },
            installedNotificationCount = readyToUpdateCount
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            navigation: StackNavigation<FapHubNavigationConfig>
        ): MainScreenDecomposeComponentImpl
    }
}