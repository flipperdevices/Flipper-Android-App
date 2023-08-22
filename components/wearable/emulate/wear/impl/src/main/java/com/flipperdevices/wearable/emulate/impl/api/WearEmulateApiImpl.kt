package com.flipperdevices.wearable.emulate.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.provideDelegate
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.keyemulate.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.api.WearEmulateApi
import com.flipperdevices.wearable.emulate.impl.composable.ComposableWearEmulate
import com.flipperdevices.wearable.setup.api.SetupApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject
import javax.inject.Provider

const val EMULATE_PATH_KEY = "path"

@ContributesBinding(AppGraph::class, WearEmulateApi::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class WearEmulateApiImpl @Inject constructor(
    private val keyEmulateUiApi: KeyEmulateUiApi,
    private val setupApi: Provider<SetupApi>
) : WearEmulateApi {
    private val featureRoute = "@$ROUTE?path={$EMULATE_PATH_KEY}"
    private val setupApiMutable by setupApi

    private val arguments = listOf(
        navArgument(EMULATE_PATH_KEY) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun open(path: String) = "@$ROUTE?path=${Uri.encode(path)}"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(featureRoute, arguments) {
            ComposableWearEmulate(
                keyEmulateUiApi,
                onNotFoundNode = {
                    navController.navigate(setupApiMutable.ROUTE.name) {
                        popUpTo(0)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
