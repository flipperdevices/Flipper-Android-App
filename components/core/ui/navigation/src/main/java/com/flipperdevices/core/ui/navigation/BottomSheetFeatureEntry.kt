package com.flipperdevices.core.ui.navigation

import android.app.Activity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
        val isLightTheme = MaterialTheme.colors.isLight

        val view = LocalView.current
        val window = (view.context as Activity).window
        val windowInsetsController = window?.let {
            WindowCompat.getInsetsController(it, view)
        }

        val statusBarInit = Color(window.statusBarColor)
        val navigationBarInit = Color(window.navigationBarColor)
        val darkIconsInit = windowInsetsController?.isAppearanceLightStatusBars == true

        val bottomSheetColor = LocalPallet.current.shareSheetBackground
        val statusBarColor = LocalPallet.current.shareSheetStatusBarColor

        val systemUIController = rememberSystemUiController()
        SideEffect {
            if (isLightTheme) {
                systemUIController.setStatusBarColor(
                    color = statusBarColor,
                    transformColorForLightContent = { statusBarColor },
                    darkIcons = true
                )
            }
            systemUIController.setNavigationBarColor(
                color = bottomSheetColor,
                transformColorForLightContent = { bottomSheetColor }
            )
        }
        DisposableEffect(key1 = Unit) {
            onDispose {
                if (isLightTheme) {
                    systemUIController.setStatusBarColor(
                        color = statusBarInit,
                        darkIcons = darkIconsInit
                    )
                }
                systemUIController.setNavigationBarColor(navigationBarInit)
            }
        }
    }

    @Composable
    fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    )
}
