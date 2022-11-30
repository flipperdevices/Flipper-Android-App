package com.flipperdevices.main.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.catalogtab.api.CatalogTabApi
import com.flipperdevices.faphub.main.api.FapHubMainScreenApi
import com.flipperdevices.faphub.search.api.FapHubSearchEntryApi
import com.flipperdevices.main.impl.composable.ComposableFapHubMainScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, FapHubMainScreenApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FapHubMainScreenApiImpl @Inject constructor(
    private val catalogTabApi: CatalogTabApi,
    private val searchEntryApi: FapHubSearchEntryApi
) : FapHubMainScreenApi {
    private fun start(): String = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableFapHubMainScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    catalogTabComposable = {
                        catalogTabApi.ComposableCatalogTab(
                            onOpenFapItem = {
                            },
                            onCategoryClick = {
                            }
                        )
                    },
                    onOpenSearch = {
                        navController.navigate(searchEntryApi.start())
                    }
                )
            }
        }
    }
}
