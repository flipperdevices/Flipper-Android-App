package com.flipperdevices.firstpair.impl.composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.firstpair.api.FirstPairFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
internal fun FirstPairNavigation(
    navController: NavHostController,
    featureEntry: FirstPairFeatureEntry,
    composeEntries: ImmutableSet<ComposableFeatureEntry>,
    aggregatesEntries: ImmutableSet<AggregateFeatureEntry>,
) {
    NavHost(
        navController = navController,
        startDestination = featureEntry.ROUTE.name
    ) {
        aggregatesEntries.forEach { featureEntry ->
            with(featureEntry) {
                navigation(navController)
            }
        }
        composeEntries.forEach { featureEntry ->
            with(featureEntry) {
                composable(navController)
            }
        }
    }
}
