package com.flipperdevices.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

interface ComposableFeatureEntry : FeatureEntry {
    val featureRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(featureRoute, arguments) { backStackEntry ->
            Composable(navController, backStackEntry)
        }
    }

    @Composable
    fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    )
}
