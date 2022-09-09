package com.flipperdevices.wearable.setup.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.wear.compose.navigation.composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.wearable.setup.api.SetupApi
import com.flipperdevices.wearable.setup.api.SetupApi.Companion.ROUTE
import com.flipperdevices.wearable.setup.impl.composable.ComposableFindPhone
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val ROUTE_START = "start"

@ContributesBinding(AppGraph::class, SetupApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class SetupApiImpl @Inject constructor() : SetupApi {
    override fun start() = ROUTE_START

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE) {
            composable(ROUTE_START) {
                ComposableFindPhone()
            }
        }
    }
}
