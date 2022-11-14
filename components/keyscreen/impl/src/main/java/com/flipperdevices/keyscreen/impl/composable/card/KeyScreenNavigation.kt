package com.flipperdevices.keyscreen.impl.composable.card

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.plusAssign
import com.flipperdevices.bridge.dao.api.model.FlipperKeyPath
import com.flipperdevices.keyscreen.impl.fragments.EXTRA_KEY_PATH
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val START_SCREEN = "key_screen"
private const val SHEET_SCREEN = "sheet_screen"
private const val SHEET_ROUTE = "@$SHEET_SCREEN?keyPath={$EXTRA_KEY_PATH}"
private fun navigationToShare(path: FlipperKeyPath?): String {
    return "@$SHEET_SCREEN?keyPath=${Uri.encode(Json.encodeToString(path))}"
}

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
internal fun KeyScreenNavigation(
    screenContent: @Composable ((FlipperKeyPath?) -> Unit) -> Unit,
    sheetContent: @Composable (() -> Unit) -> Unit
) {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
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
                    navController.navigate(navigationToShare(flipperKeyPath))
                }
            }
            bottomSheet(
                route = SHEET_ROUTE,
                arguments = listOf(
                    navArgument(EXTRA_KEY_PATH) {
                        type = FlipperKeyPathType()
                        nullable = true
                    }
                )
            ) {
                sheetContent {
                    navController.popBackStack()
                }
            }
        }
    }
}

class FlipperKeyPathType : NavType<FlipperKeyPath>(isNullableAllowed = true) {
    override fun get(bundle: Bundle, key: String): FlipperKeyPath? {
        return bundle.getParcelable(key)
    }

    override fun parseValue(value: String): FlipperKeyPath {
        return Json.decodeFromString(value)
    }

    override fun put(bundle: Bundle, key: String, value: FlipperKeyPath) {
        bundle.putParcelable(key, value)
    }
}
