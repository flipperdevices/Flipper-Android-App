package com.flipperdevices.screenstreaming.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.screenstreaming.api.ScreenStreamingFeatureEntry
import com.flipperdevices.screenstreaming.impl.composable.ComposableScreen
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, ScreenStreamingFeatureEntry::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class ScreenStreamingFeatureEntryImpl @Inject constructor() : ScreenStreamingFeatureEntry {
    private fun start() = "@${ROUTE.name}"

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE.name) {
            composable("@${ROUTE.name}") {
                ComposableScreen()
            }
        }
    }
}
