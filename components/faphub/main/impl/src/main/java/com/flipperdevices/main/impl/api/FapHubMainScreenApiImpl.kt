package com.flipperdevices.main.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.faphub.main.api.FapHubMainScreenApi
import com.flipperdevices.main.impl.composable.ComposableFapHubMainScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding


@ContributesBinding(AppGraph::class, FapHubMainScreenApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class FapHubMainScreenApiImpl : FapHubMainScreenApi {
    private fun start(): String = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableFapHubMainScreen()
            }
        }
    }
}