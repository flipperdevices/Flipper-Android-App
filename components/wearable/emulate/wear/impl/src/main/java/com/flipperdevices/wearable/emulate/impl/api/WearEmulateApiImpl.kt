package com.flipperdevices.wearable.emulate.impl.api

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
    override val featureRoute = "wearosemulate?path={$EMULATE_PATH_KEY}"

    override val arguments = listOf(
        navArgument(EMULATE_PATH_KEY) {
            type = NavType.StringType
            nullable = false
        }
    )

    override fun open(path: String) = "wearosemulate?path=${Uri.encode(path)}"

    @Composable
    override fun NavGraphBuilder.Composable(
        navController: NavHostController,
        backStackEntry: NavBackStackEntry
    ) {
        ComposableWearEmulate(
            keyEmulateUiApi,
            onNotFoundNode = {
                navController.navigate(
                    ComponentHolder.component<WearEmulateComponent>().setupApi.get().ROUTE.name
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
