package com.flipperdevices.keyscreen.impl.composable.card

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.share.api.ShareBottomFeatureEntry
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

private const val START_SCREEN = "key_screen"

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun KeyScreenNavigation(
    bottomSheetFeatureEntry: ShareBottomFeatureEntry,
    screenContent: @Composable ((FlipperKeyPath?) -> Unit) -> Unit
) {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    ModalBottomSheetLayout(bottomSheetNavigator = bottomSheetNavigator) {
        NavHost(
            navController = navController,
            startDestination = START_SCREEN
        ) {
            composable(
                route = START_SCREEN
            ) {
                screenContent { flipperKeyPath ->
                    navController.navigate(
                        bottomSheetFeatureEntry.shareDestination(flipperKeyPath)
                    )
                }
            }
            with(bottomSheetFeatureEntry) {
                bottomSheet(navController)
            }
        }
    }
}
