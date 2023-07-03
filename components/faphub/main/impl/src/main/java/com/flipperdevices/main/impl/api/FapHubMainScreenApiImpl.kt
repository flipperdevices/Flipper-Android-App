package com.flipperdevices.main.impl.api

import android.content.Intent
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.category.api.FapHubCategoryApi
import com.flipperdevices.faphub.fapscreen.api.FapScreenApi
import com.flipperdevices.faphub.installedtab.api.FapInstalledApi
import com.flipperdevices.faphub.main.api.FapHubHandleDeeplink
import com.flipperdevices.faphub.main.api.FapHubMainScreenApi
import com.flipperdevices.faphub.search.api.FapHubSearchEntryApi
import com.flipperdevices.main.impl.composable.ComposableFapHubMainScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(AppGraph::class, FapHubHandleDeeplink::class)
@ContributesBinding(AppGraph::class, FapHubMainScreenApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FapHubMainScreenApiImpl @Inject constructor(
    private val catalogTabApi: CatalogTabApi,
    private val searchEntryApi: FapHubSearchEntryApi,
    private val categoryEntryApi: FapHubCategoryApi,
    private val fapScreenApi: FapScreenApi,
    private val installedApi: FapInstalledApi
) : FapHubMainScreenApi, FapHubHandleDeeplink {
    private fun start(): String = "@${ROUTE.name}"

    private var rootNavHostController: NavHostController? = null

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        rootNavHostController = navController

        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                val readyToUpdateCount = installedApi.getUpdatePendingCount()
                ComposableFapHubMainScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    catalogTabComposable = {
                        catalogTabApi.ComposableCatalogTab(
                            onOpenFapItem = {
                                navController.navigate(fapScreenApi.getFapScreen(it.id))
                            },
                            onCategoryClick = {
                                navController.navigate(categoryEntryApi.open(it))
                            }
                        )
                    },
                    installedTabComposable = {
                        installedApi.ComposableInstalledTab(onOpenFapItem = {
                            navController.navigate(fapScreenApi.getFapScreen(it))
                        })
                    },
                    onOpenSearch = {
                        navController.navigate(searchEntryApi.start())
                    },
                    installedNotificationCount = readyToUpdateCount
                )
            }
        }
    }

    override fun handleDeepLink(intent: Intent) {
        rootNavHostController?.handleDeepLink(intent)
    }
}
