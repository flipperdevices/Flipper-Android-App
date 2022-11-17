package com.flipperdevices.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.bottomSheet

interface BottomSheetFeatureEntry : FeatureEntry {
    val featureRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    fun NavGraphBuilder.bottomSheet(navController: NavHostController) {
        bottomSheet(
            route = featureRoute,
            arguments
        ) { backStackEntry ->
            Composable(navController, backStackEntry)
        }
    }

    @Composable
    fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    )
}
