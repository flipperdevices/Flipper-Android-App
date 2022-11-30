package com.flipperdevices.faphub.search.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.search.api.FapHubSearchEntryApi
import com.flipperdevices.faphub.search.impl.composable.ComposableSearchScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject


@ContributesBinding(AppGraph::class, FapHubSearchEntryApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FapHubSearchEntryApiImpl @Inject constructor(

) : FapHubSearchEntryApi {
    override fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableSearchScreen(
                    onBack = { navController.popBackStack() },
                    onFapItemClick = {

                    }
                )
            }
        }
    }
}