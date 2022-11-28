package com.flipperdevices.core.ui.navigation

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.flipperdevices.core.ui.theme.LocalPallet
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.systemuicontroller.rememberSystemUiController

interface BottomSheetFeatureEntry : FeatureEntry {
    val featureRoute: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    // use color for setup and reset status bar color,
    // because current realization bottom sheet dont have that
    // https://stackoverflow.com/questions/69560253/modal-bottom-sheet-scrim-color-is-not-shown-in-status-bar-in-jetpack-compose?
    fun NavGraphBuilder.bottomSheet(
        navController: NavHostController
    ) {
        bottomSheet(
            route = featureRoute,
            arguments = arguments
        ) { backStackEntry ->
            ProcessSystemBar()
            Composable(navController, backStackEntry)
        }
    }

    @Composable
    private fun ProcessSystemBar() {
        val view = LocalView.current
        val statusBarColorInit = (view.context as Activity).window.statusBarColor
        val navBarColorInit = (view.context as Activity).window.navigationBarColor

        val scrimColor = LocalPallet.current.scrimColor
        val bottomSheetColor = LocalPallet.current.shareSheetBackground

        val isLightTheme = MaterialTheme.colors.isLight

        val systemUIController = rememberSystemUiController()
        SideEffect {
            if (isLightTheme) {
                systemUIController.setStatusBarColor(
                    color = scrimColor,
                    darkIcons = true,
                    transformColorForLightContent = { scrimColor }
                )
            }
            systemUIController.setNavigationBarColor(
                color = bottomSheetColor,
                darkIcons = true,
                transformColorForLightContent = { bottomSheetColor }
            )
        }
        DisposableEffect(key1 = Unit) {
            onDispose {
                if (isLightTheme) {
                    systemUIController.setStatusBarColor(Color(statusBarColorInit))
                }
                systemUIController.setNavigationBarColor(Color(navBarColorInit))
            }
        }
    }

    @Composable
    fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    )
}
