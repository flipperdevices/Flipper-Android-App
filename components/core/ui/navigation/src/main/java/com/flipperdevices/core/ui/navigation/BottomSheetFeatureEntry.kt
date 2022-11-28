package com.flipperdevices.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.systemuicontroller.rememberSystemUiController

const val SCRIM = 0.15f

interface BottomSheetFeatureEntry : FeatureEntry {
    val featureRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    // use color for setup and reset status bar color,
    // because current realization bottom sheet dont have that
    // https://stackoverflow.com/questions/69560253/modal-bottom-sheet-scrim-color-is-not-shown-in-status-bar-in-jetpack-compose?
    fun NavGraphBuilder.bottomSheet(
        navController: NavHostController,
        initStatusBarColor: Color,
        scrimColor: Color
    ) {
        bottomSheet(
            route = featureRoute,
            arguments
        ) { backStackEntry ->
            val systemUIController = rememberSystemUiController()
            SideEffect {
                systemUIController.setStatusBarColor(scrimColor)
            }
            DisposableEffect(key1 = Unit) {
                onDispose {
                    systemUIController.setStatusBarColor(initStatusBarColor)
                }
            }
            Composable(navController, backStackEntry)
        }
    }

    @Composable
    fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    )
}
