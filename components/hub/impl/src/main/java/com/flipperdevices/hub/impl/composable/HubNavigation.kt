package com.flipperdevices.hub.impl.composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.hub.impl.api.HubFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun HubNavigation(
    navController: NavHostController,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    composableEntries: ImmutableSet<ComposableFeatureEntry>,
    hubFeatureEntry: HubFeatureEntry
) {
    NavHost(
        navController = navController,
        startDestination = hubFeatureEntry.ROUTE.name
    ) {
        featureEntries.forEach {
            with(it) {
                navigation(navController)
            }
        }
        composableEntries.forEach {
            with(it) {
                composable(navController)
            }
        }
    }
}
