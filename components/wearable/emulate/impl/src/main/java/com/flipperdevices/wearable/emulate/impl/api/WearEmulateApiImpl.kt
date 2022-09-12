package com.flipperdevices.wearable.emulate.impl.api

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.wear.compose.material.Text
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.ComposableFeatureEntry
import com.flipperdevices.wearable.emulate.api.WearEmulateApi
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

@ContributesBinding(AppGraph::class, WearEmulateApi::class)
@ContributesMultibinding(AppGraph::class, ComposableFeatureEntry::class)
class WearEmulateApiImpl @Inject constructor() : WearEmulateApi {
    override val featureRoute = "wearosemulate?path={path}"

    override val arguments = listOf(
        navArgument("path") {
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
        Text("Test")
    }
}
