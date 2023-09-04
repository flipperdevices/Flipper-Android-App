package com.flipperdevices.singleactivity.impl.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.createGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun ComposableSingleActivityNavHost(
    navController: NavHostController,
    startDestination: String,
    featureEntries: ImmutableSet<AggregateFeatureEntry>,
    composableEntries: ImmutableSet<ComposableFeatureEntry>,
    modifier: Modifier = Modifier,
) {
    val graph = remember(startDestination, featureEntries, composableEntries) {
        navController.createGraph(startDestination, null) {
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

    NavHost(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        graph = graph,
        navController = navController
    )
}
