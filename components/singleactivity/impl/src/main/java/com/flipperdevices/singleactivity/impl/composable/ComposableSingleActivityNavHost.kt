package com.flipperdevices.singleactivity.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.flipperdevices.bottombar.api.BottomNavigationFeatureEntry
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun ComposableSingleActivityNavHost(
    navController: NavHostController,
    bottomNavigationFeatureEntry: BottomNavigationFeatureEntry,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    composableEntries: ImmutableSet<ComposableFeatureEntry>,
    modifier: Modifier = Modifier,
) {
    NavHost(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        navController = navController,
        startDestination = bottomNavigationFeatureEntry.start()
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
