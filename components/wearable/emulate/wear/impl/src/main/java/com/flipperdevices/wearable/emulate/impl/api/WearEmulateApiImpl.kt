package com.flipperdevices.wearable.emulate.impl.api

import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.di.ComponentHolder
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.keyscreen.api.KeyEmulateUiApi
import com.flipperdevices.wearable.emulate.api.WearEmulateApi
import com.flipperdevices.wearable.emulate.impl.composable.ComposableWearEmulate
import com.flipperdevices.wearable.emulate.impl.di.WearEmulateComponent
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

const val EMULATE_PATH_KEY = "path"

@ContributesBinding(AppGraph::class, WearEmulateApi::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class WearEmulateApiImpl @Inject constructor(
    private val keyEmulateUiApi: KeyEmulateUiApi
) : WearEmulateApi {
    private val featureRoute = "@${ROUTE}?path={$EMULATE_PATH_KEY}"

    private val arguments = listOf(
        navArgument(EMULATE_PATH_KEY) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun open(path: String) = "@${ROUTE}?path=${Uri.encode(path)}"

    override fun NavGraphBuilder.composable(navController: NavHostController) {
        composable(featureRoute, arguments) {
            ComposableWearEmulate(
                keyEmulateUiApi,
                onNotFoundNode = {
                    navController.navigate(
                        ComponentHolder.component<WearEmulateComponent>().setupApi.ROUTE.name
                    ) {
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
