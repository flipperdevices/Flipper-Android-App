package com.flipperdevices.archive.impl.composable

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flipperdevices.archive.api.ArchiveFeatureEntry
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
internal fun ArchiveNavigation(
    navController: NavHostController,
    featureEntry: ArchiveFeatureEntry,
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
            println("TEST1 $featureEntry")
            with(featureEntry) {
                composable(navController)
            }
        }
    }
}
