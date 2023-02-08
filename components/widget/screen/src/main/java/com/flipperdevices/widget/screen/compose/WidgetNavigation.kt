package com.flipperdevices.widget.screen.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.widget.api.WidgetFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
internal fun WidgetNavigation(
    navController: NavHostController,
    widgetId: Int,
    featureEntry: WidgetFeatureEntry,
    composeEntries: ImmutableSet<ComposableFeatureEntry>,
    featureEntries: ImmutableSet<AggregateFeatureEntry>
) {
    NavHost(
        navController = navController,
        startDestination = featureEntry.ROUTE.name
    ) {
        featureEntries.forEach {
            with(it) {
                navigation(navController)
            }
        }
        composeEntries.forEach {
            with(it) {
                composable(navController)
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        val widgetScreen = featureEntry.getWidgetScreen(widgetId)
        navController.navigate(widgetScreen)
    }
}
