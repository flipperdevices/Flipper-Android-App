package com.flipperdevices.wearable.sync.wear.impl.api

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import androidx.wear.compose.navigation.composable
import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.core.ui.navigation.AggregateFeatureEntry
import com.flipperdevices.wearable.emulate.api.WearEmulateApi
import com.flipperdevices.wearable.sync.wear.api.KeysListApi
import com.flipperdevices.wearable.sync.wear.api.KeysListApi.Companion.ROUTE
import com.flipperdevices.wearable.sync.wear.impl.composable.ComposableKeysList
import com.squareup.anvil.annotations.ContributesBinding
import com.squareup.anvil.annotations.ContributesMultibinding
import javax.inject.Inject

private const val ROUTE_START = "start"

@ContributesBinding(AppGraph::class, KeysListApi::class)
@ContributesMultibinding(AppGraph::class, AggregateFeatureEntry::class)
class KeysListApiImpl @Inject constructor(
    val emulateApi: WearEmulateApi
) : KeysListApi {
    override fun start() = ROUTE_START

    override fun NavGraphBuilder.navigation(navController: NavHostController) {
        navigation(startDestination = start(), route = ROUTE) {
            composable(ROUTE_START) {
                ComposableKeysList() {
                    navController.navigate(emulateApi.open(it.path.path.pathToKey))
                }
            }
        }
    }
}
