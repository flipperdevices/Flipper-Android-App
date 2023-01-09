package com.flipperdevices.info.impl.compose

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.info.impl.api.InfoFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun InfoNavigation(
    navController: NavHostController,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    infoFeatureEntry: InfoFeatureEntry
) {
    NavHost(
        navController = navController,
        startDestination = infoFeatureEntry.ROUTE.name
    ) {
        featureEntries.forEach {
            with(it) {
                navigation(navController)
            }
        }
    }
}
