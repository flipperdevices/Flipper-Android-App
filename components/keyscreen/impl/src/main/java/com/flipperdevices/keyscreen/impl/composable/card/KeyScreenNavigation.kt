package com.flipperdevices.keyscreen.impl.composable.card

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.core.ui.navigation.SCRIM
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
    val view = LocalView.current
    val statusBarColorInternal = (view.context as Activity).window.statusBarColor
    val scrimColor = MaterialTheme.colors.onSurface.copy(alpha = SCRIM)
    val statusBarColor = Color(statusBarColorInternal)

    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        scrimColor = scrimColor,
        sheetShape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)
    ) {
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
                bottomSheet(navController, statusBarColor, scrimColor)
            }
        }
    }
}
